package net.rubygrapefruit.file

sealed class FileSystemElementMetadata

object MissingEntryMetadata : FileSystemElementMetadata()

object UnreadableEntryMetadata : FileSystemElementMetadata()

object DirectoryMetadata : FileSystemElementMetadata()

object SymlinkMetadata : FileSystemElementMetadata()

object OtherMetadata : FileSystemElementMetadata()

data class RegularFileMetadata(val size: ULong) : FileSystemElementMetadata()
