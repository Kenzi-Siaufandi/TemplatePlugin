#!/usr/bin/env bash
set -euo pipefail

cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

if [[ ${1:-} == --help ]]; then
    echo "Usage: ./setup.sh [group-id] [plugin-name]"
    echo "Example: ./setup.sh io.github.yourname Duels"
    echo "Omit arguments to choose values interactively. Requires Bash and GNU sed."
    exit 0
fi
if (( $# > 2 )); then
    echo "Usage: ./setup.sh [group-id] [plugin-name]" >&2
    exit 1
fi

for file in build.gradle.kts settings.gradle.kts src/main/resources/paper-plugin.yml; do
    if [[ ! -f $file || ! -w $file ]]; then
        echo "Error: Missing or unwritable $file; nothing changed." >&2
        exit 1
    fi
done

old_group=$(sed -n 's/^group = "\([^"]*\)"/\1/p' build.gradle.kts)
old_name=$(sed -n 's/^name: //p' src/main/resources/paper-plugin.yml)
case "$old_name" in
    \'*\'|\"*\") old_name=${old_name:1:${#old_name}-2} ;;
esac
old_main=$(sed -n 's/^main: .*\.//p' src/main/resources/paper-plugin.yml)
old_loader=$(sed -n 's/^loader: .*\.//p' src/main/resources/paper-plugin.yml)

if [[ -z $old_group || -z $old_name || -z $old_main || -z $old_loader ]]; then
    echo "Error: Could not read the current group, plugin name, main, or loader." >&2
    exit 1
fi

group=${1:-}
name=${2:-}
if [[ -z $group ]]; then
    read -r -p "Group / Java package [$old_group]: " group
    group=${group:-$old_group}
fi
if [[ -z $name ]]; then
    read -r -p "Plugin name (e.g. Duels) [$old_name]: " name
    name=${name:-$old_name}
fi

if [[ ! $group =~ ^[a-z_][a-z0-9_]*(\.[a-z_][a-z0-9_]*)*$ || ! $name =~ ^[A-Z][A-Za-z0-9]*$ ]]; then
    echo "Error: Use a lowercase Java package (io.github.yourname) and a name like Duels." >&2
    exit 1
fi
IFS='.' read -r -a package_parts <<< "$group"
for part in "${package_parts[@]}"; do
    case "$part" in
        abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while|true|false|null|_)
            echo "Error: '$part' cannot be a Java package segment." >&2
            exit 1
            ;;
    esac
done

main="${name}Plugin"
loader="${name}PluginLoader"
old_dir="src/main/java/${old_group//.//}"
new_dir="src/main/java/${group//.//}"

for class in "$old_main" "$old_loader"; do
    if [[ ! -f "$old_dir/$class.java" ]]; then
        echo "Error: Missing $old_dir/$class.java" >&2
        exit 1
    fi
done
for pair in "$old_main:$main" "$old_loader:$loader"; do
    source="$old_dir/${pair%%:*}.java"
    target="$new_dir/${pair#*:}.java"
    if [[ $source != "$target" && -e $target ]]; then
        echo "Error: $target already exists; nothing changed." >&2
        exit 1
    fi
done

mkdir -p -- "$new_dir"

# Escape dots so sed matches the package literally.
escaped_group=${old_group//./\\.}
while IFS= read -r -d '' file; do
    sed -i \
        -e "s/\(^\|[^[:alnum:]_$.]\)${escaped_group}\b/\1$group/g" \
        -e "s/\b${old_main}\b/$main/g" \
        -e "s/\b${old_loader}\b/$loader/g" \
        "$file"
done < <(find src -type f \( -name '*.java' -o -name '*.yml' -o -name '*.yaml' \) -print0)

sed -i "s/^group = .*/group = \"$group\"/" build.gradle.kts
sed -i "s/^rootProject.name = .*/rootProject.name = \"$name\"/" settings.gradle.kts
sed -i "s/^name: .*/name: '$name'/" src/main/resources/paper-plugin.yml

if [[ "$old_dir/$old_main.java" != "$new_dir/$main.java" ]]; then
    mv -- "$old_dir/$old_main.java" "$new_dir/$main.java"
fi
if [[ "$old_dir/$old_loader.java" != "$new_dir/$loader.java" ]]; then
    mv -- "$old_dir/$old_loader.java" "$new_dir/$loader.java"
fi
if [[ $old_dir != "$new_dir" ]]; then
    # Remove only empty old package directories, leaving other source files alone.
    rmdir -p -- "$old_dir" 2>/dev/null || true
fi

echo "Configured $name: $group.$main (loader: $group.$loader)."
echo "Run ./gradlew build to verify the project."
