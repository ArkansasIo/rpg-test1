#pragma once

#include "CoreMinimal.h"

// Execute Python code inside the Unreal Editor (requires Editor Scripting - Python enabled)
void ExecutePythonInEditor(const FString& PythonCode);

// Open a file in the default external editor
void OpenFileInDefaultEditor(const FString& RelativePath);
