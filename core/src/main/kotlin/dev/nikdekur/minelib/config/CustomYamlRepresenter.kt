package dev.nikdekur.minelib.config

import org.bukkit.configuration.file.YamlRepresenter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.nodes.MappingNode
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.NodeTuple
import org.yaml.snakeyaml.nodes.Tag

open class CustomYamlRepresenter : YamlRepresenter() {
    // Флаг, который указывает, что сейчас обрабатывается ключ.
    private var isKey: Boolean = false

    // Переопределяем представление скаляра:
    override fun representScalar(tag: Tag, value: String, style: Char?): Node {
        // Если скаляр является строкой и это не ключ, задаём стиль DOUBLE_QUOTED.
        return if (tag == Tag.STR && !isKey) {
            super.representScalar(tag, value, DumperOptions.ScalarStyle.DOUBLE_QUOTED.char)
        } else {
            super.representScalar(tag, value, null)
        }
    }

    // Переопределяем представление отображения (Map)
    override fun representMapping(tag: Tag, mapping: Map<*, *>, flowStyle: Boolean?): MappingNode {
        val nodeValue = mutableListOf<NodeTuple>()
        for ((key, value) in mapping) {
            // Обрабатываем ключ: устанавливаем флаг isKey в true
            isKey = true
            val keyNode = representData(key)
            // Сбрасываем флаг для значения
            isKey = false
            val valueNode = representData(value)

            nodeValue.add(NodeTuple(keyNode, valueNode))
        }

        return MappingNode(tag, nodeValue, flowStyle).also {
            if (flowStyle == null) {
                it.flowStyle = defaultFlowStyle.styleBoolean
            }
        }
    }
}