package mah.plugin.support.search

import mah.common.util.XmlUtils
import mah.openapi.ui.layout.OpenClassicItemListLayout
import mah.openapi.util.DesktopUtils
import mah.plugin.command.PluginCommandSupport
import mah.plugin.config.XmlConfigurable
import mah.ui.icon.Icon
import mah.ui.pane.item.FullItemImpl
import mah.ui.window.WindowManager
import org.w3c.dom.Node


/**
 * Created by zgq on 2/27/17.
 */
class GoogleSearchCommand : PluginCommandSupport(), XmlConfigurable {
    val layout: OpenClassicItemListLayout;

    init {
        layout = layoutFactory.createClassicItemListLayout()
        addInitializeEventHandler {
            layout.setOnInputConfirmed {
                val input = it.input
                val commands = input.split(" ")
                val query = commands[1]
                val url = "https://www.google.com/#q=" + query + "&*"
                DesktopUtils.openBrowser(url)
                WindowManager.getInstance().currentWindow.hide()
                Runtime.getRuntime().exec(postCommand);
            }
        }
        addCommonFilterEventHandler {
            val content = "搜索 " + it.content
            layout.showFirstItem(FullItemImpl.Builder(content)
                    .icon(Icon.valueOf("search/google.png"))
                    .description("回车搜索")
                    .build())
        }
    }

    private var postCommand: String? = null;

    override fun configure(node: Node?) {
        postCommand = XmlUtils.getChildNodeText(node, "postCommand")
    }

    override fun getName(): String {
        return "GoogleSearchCommand";
    }
}
