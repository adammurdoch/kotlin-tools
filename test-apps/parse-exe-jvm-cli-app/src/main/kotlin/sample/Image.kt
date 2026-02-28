package sample

sealed class Image

data class MachOImage(val cpu: CPU): Image()

data class CPU(val description: String)
