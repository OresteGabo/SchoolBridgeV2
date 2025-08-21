#!/bin/bash

# Base package path
BASE_PATH="com/bridge/school"

# Core structure
mkdir -p "$BASE_PATH/app"
mkdir -p "$BASE_PATH/core/components"
mkdir -p "$BASE_PATH/core/preferences"
mkdir -p "$BASE_PATH/core/theme"
mkdir -p "$BASE_PATH/core/session"
mkdir -p "$BASE_PATH/core/utils"
mkdir -p "$BASE_PATH/core/localization"
mkdir -p "$BASE_PATH/core/di"

# Features
FEATURES=(auth finance messaging user school timetable)
SUBPACKAGES=(data/domain/presentation/di)

for feature in "${FEATURES[@]}"; do
  for part in "${SUBPACKAGES[@]}"; do
    mkdir -p "$BASE_PATH/features/$feature/$part"
  done

  # Extra subfolders for data and domain
  mkdir -p "$BASE_PATH/features/$feature/data/dto"
  mkdir -p "$BASE_PATH/features/$feature/data/mapper"
  mkdir -p "$BASE_PATH/features/$feature/data/repository"

  mkdir -p "$BASE_PATH/features/$feature/domain/model"
  mkdir -p "$BASE_PATH/features/$feature/domain/usecase"
done

# Navigation
mkdir -p "$BASE_PATH/navigation"

# Optional mappers root
mkdir -p "$BASE_PATH/mapper"

# Confirm
echo "Package structure under 'src/com/bridge/school/' created successfully."
