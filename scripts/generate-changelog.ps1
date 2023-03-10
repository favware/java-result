Function Generate-Changelog
{
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $True, ValueFromRemainingArguments = $False)]
        $Tag
    )

    Process {
        $RootDirectory = "$PSScriptRoot\.."
        git cliff --tag $Tag --prepend $RootDirectory/CHANGELOG.md -u -c $RootDirectory/cliff.toml
    }
}

Generate-Changelog @Args
