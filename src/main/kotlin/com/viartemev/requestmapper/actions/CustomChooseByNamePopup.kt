package com.viartemev.requestmapper.actions

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.ide.IdeEventQueue
import com.intellij.ide.util.gotoByName.ChooseByNameBase
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.ChooseByNameModel
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.popup.AbstractPopup
import java.awt.Component
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.lang.reflect.Field
import java.lang.reflect.Method
import javax.swing.*

open class CustomChooseByNamePopup protected constructor(
	project: Project?, model: ChooseByNameModel, provider: ChooseByNameItemProvider,
	oldPopup: ChooseByNamePopup?, predefinedText: String?, mayRequestOpenInCurrentWindow: Boolean, initialIndex: Int
) : ChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex) {

	override fun showTextFieldPanel() {
		val myTextFieldPanel = myTextFieldPanelField.get(this) as? JPanelProvider ?: return
		val layeredPane = getLayeredPane()
		val preferredTextFieldPanelSize = myTextFieldPanel.preferredSize
		val x = (layeredPane.width - preferredTextFieldPanelSize.width) / 2
		val y = layeredPane.height / 4 - preferredTextFieldPanelSize.height
		val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(myTextFieldPanel, myTextField)
		builder.setLocateWithinScreenBounds(false)
		builder.setKeyEventHandler { event: KeyEvent? ->
			val myTextPopup = myTextPopupField.get(this) as? JBPopup
			if (myTextPopup == null || !AbstractPopup.isCloseRequest(event) || !myTextPopup.isCancelKeyEnabled) {
				return@setKeyEventHandler false
			}
			val focusManager = IdeFocusManager.getInstance(myProject)
			if (isDescendingFromTemporarilyFocusableToolWindow(focusManager.focusOwner)) {
				focusManager.requestFocus(myTextField, true)
				return@setKeyEventHandler false
			} else {
				myTextPopup.cancel(event)
				return@setKeyEventHandler true
			}
		}.setCancelCallback {
			myTextPopupField.set(this, null)
			close(false)
			true
		}.setFocusable(true).setRequestFocus(true).setModalContext(false).setCancelOnClickOutside(false)
		builder.createPopup().apply {
			myTextPopupField.set(this@CustomChooseByNamePopup,this)
			val point = Point(x, y)
			SwingUtilities.convertPointToScreen(point, layeredPane)
			val bounds = Rectangle(point, Dimension(preferredTextFieldPanelSize.width + 20, preferredTextFieldPanelSize.height))
			size = bounds.size
			setLocation(bounds.location)
			if (myProject != null && !myProject!!.isDefault) {
				DaemonCodeAnalyzer.getInstance(myProject).disableUpdateByTimer(this)
			}
			Disposer.register(this) { cancelListUpdaterMethod.invoke(this@CustomChooseByNamePopup) }
			IdeEventQueue.getInstance().popupManager.closeAllPopups(false)
			show(layeredPane)
		}
	}

	private fun getLayeredPane(): JLayeredPane {
		val layeredPane: JLayeredPane
		val window = WindowManager.getInstance().suggestParentWindow(myProject)
		layeredPane = when (window) {
			is JFrame -> window.layeredPane
			is JDialog -> window.layeredPane
			is JWindow -> window.layeredPane
			else -> throw IllegalStateException("cannot find parent window: project=$myProject${if (myProject != null) "; open=" + myProject!!.isOpen else ""}; window=$window")
		}
		return layeredPane
	}

	private fun isDescendingFromTemporarilyFocusableToolWindow(component: Component?): Boolean {
		if (component == null || myProject == null || myProject!!.isDisposed) return false
		val toolWindowManager = ToolWindowManager.getInstance(myProject!!)
		val activeToolWindowId = toolWindowManager.activeToolWindowId
		val toolWindow = if (activeToolWindowId == null) null else toolWindowManager.getToolWindow(activeToolWindowId)
		val toolWindowComponent = toolWindow?.component
		return toolWindowComponent?.getClientProperty(TEMPORARILY_FOCUSABLE_COMPONENT_KEY) != null &&
				SwingUtilities.isDescendingFrom(component, toolWindowComponent)
	}

	companion object {
		val myTextPopupField: Field = ChooseByNameBase::class.java.getDeclaredField("myTextPopup").apply { isAccessible = true }
		val myTextFieldPanelField: Field = ChooseByNameBase::class.java.getDeclaredField("myTextFieldPanel").apply { isAccessible = true }
		val cancelListUpdaterMethod: Method = ChooseByNameBase::class.java.getDeclaredMethod("cancelListUpdater").apply { isAccessible = true }

		fun createPopup(
			project: Project?,
			model: ChooseByNameModel,
			provider: ChooseByNameItemProvider,
			predefinedText: String?,
			mayRequestOpenInCurrentWindow: Boolean,
			initialIndex: Int
		): CustomChooseByNamePopup {
			val oldPopup = project?.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY)
			oldPopup?.close(false)
			val newPopup = CustomChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex)
			project?.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup)
			return newPopup
		}
	}
}