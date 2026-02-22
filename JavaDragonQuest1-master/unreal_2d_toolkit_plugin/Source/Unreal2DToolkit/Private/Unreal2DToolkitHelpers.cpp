#include "Unreal2DToolkitHelpers.h"
#include "Misc/Paths.h"
#include "HAL/PlatformProcess.h"

#if WITH_EDITOR
#include "Editor.h"
#endif

void ExecutePythonInEditor(const FString& PythonCode)
{
#if WITH_EDITOR
    if (GEditor)
    {
        FString Cmd = FString::Printf(TEXT("py %s"), *PythonCode.Replace(TEXT("\n"), TEXT("; ")));
        GEditor->Exec(nullptr, *Cmd);
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("GEditor is null; cannot execute Python command: %s"), *PythonCode);
    }
#else
    UE_LOG(LogTemp, Warning, TEXT("Attempted to execute Python code outside editor: %s"), *PythonCode);
#endif
}

void OpenFileInDefaultEditor(const FString& RelativePath)
{
    FString BaseDir = FPaths::ConvertRelativePathToFull(FPaths::ProjectDir());
    FString FullPath = FPaths::Combine(BaseDir, RelativePath);
    if (FPaths::FileExists(FullPath))
    {
        FPlatformProcess::LaunchFileInDefaultExternalApplication(*FullPath, nullptr, ELaunchVerb::Open);
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("File not found: %s"), *FullPath);
    }
}
