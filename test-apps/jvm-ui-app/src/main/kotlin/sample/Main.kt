package sample

import sample.calc.Failure
import sample.calc.Parser
import sample.calc.Success
import java.awt.Color
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun main() {
    val frame = JFrame("Test App")
    frame.size = Dimension(500, 400)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val main = JPanel()
    main.layout = GridBagLayout()
    main.isOpaque = true
    main.background = Color.WHITE
    main.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    frame.contentPane.add(main)

    val input = JTextField()
    main.add(JLabel("Input:"), GridBagConstraints().also {
        it.gridx = 0
        it.gridy = 0
    })
    main.add(input, GridBagConstraints().also {
        it.gridx = 1
        it.gridy = 0
        it.fill = GridBagConstraints.HORIZONTAL
        it.weightx = 1.0
    })

    val expressionLabel = JLabel("")
    main.add(JLabel("Expression:"), GridBagConstraints().also {
        it.gridx = 0
        it.gridy = 1
    })
    main.add(expressionLabel, GridBagConstraints().also {
        it.gridx = 1
        it.gridy = 1
        it.weightx = 1.0
    })

    val resultLabel = JLabel("")
    main.add(JLabel("Result:"), GridBagConstraints().also {
        it.gridx = 0
        it.gridy = 2
        it.weighty = 1.0
    })
    main.add(resultLabel, GridBagConstraints().also {
        it.gridx = 1
        it.gridy = 2
        it.weightx = 1.0
        it.weighty = 1.0
    })

    input.document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            update()
        }

        override fun removeUpdate(e: DocumentEvent?) {
            update()
        }

        override fun changedUpdate(e: DocumentEvent?) {
            update()
        }

        fun update() {
            val parser = Parser()
            val result = parser.parse(arrayOf(input.text))
            when (result) {
                is Success -> {
                    expressionLabel.text = result.expression.toString()
                    resultLabel.text = result.expression.evaluate().toString()
                }

                is Failure -> {
                    expressionLabel.text = result.message
                    resultLabel.text = ""
                }
            }
        }
    })

    val screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.bounds
    val x = (screenBounds.width - screenBounds.x - frame.width) / 2 + screenBounds.x
    val y = screenBounds.height / 3 + screenBounds.y
    frame.setLocation(x, y)

    frame.isVisible = true
}
