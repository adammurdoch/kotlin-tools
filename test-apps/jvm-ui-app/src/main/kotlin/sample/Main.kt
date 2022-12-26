package sample

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

fun main() {
    val frame = JFrame("Test App")
    frame.size = Dimension(500, 400)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val main = JPanel()
    main.layout = BorderLayout()
    main.isOpaque = true
    frame.contentPane.add(main)

    val label = JLabel("Hello")
    main.add(label, BorderLayout.CENTER)

    frame.isVisible = true
}
