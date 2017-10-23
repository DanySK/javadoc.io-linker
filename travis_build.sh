#!/bin/bash
set -e
openssl aes-256-cbc -K $encrypted_62b5a1c46bb0_key -iv $encrypted_62b5a1c46bb0_iv -in prepare_environment.sh.enc -out prepare_environment.sh -d
sh prepare_environment.sh
./gradlew check projectReport uploadArchives
./gradlew publishPlugins --continue || exit 0
