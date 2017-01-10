package mah.ui.support.swing.layout;

import mah.ui.input.InputMode;
import mah.ui.layout.ClassicAbstractLayout;
import mah.ui.layout.LayoutFactoryBean;
import mah.ui.pane.Pane;
import mah.ui.pane.input.InputPane;
import mah.ui.support.swing.pane.SwingPane;
import mah.ui.support.swing.pane.input.InputPaneFactory;
import mah.ui.support.swing.pane.input.InputPaneImpl;
import mah.ui.support.swing.theme.LayoutThemeImpl;
import mah.ui.theme.LayoutTheme;
import mah.ui.theme.Themeable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Created by zgq on 2017-01-08 11:56
 */
public class ClassicAbstractLayoutImpl extends SwingLayoutSupport implements ClassicAbstractLayout {

    private static ClassicAbstractLayoutImpl instance;
    private JPanel panel;
    private InputPaneImpl inputPane;
    private SwingPane bottomPane;
    private LayoutThemeImpl currentTheme;
    private boolean init;

    private ClassicAbstractLayoutImpl() {

    }

    @Override
    public void init() {
        this.panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        this.panel.setLayout(boxLayout);
        this.inputPane = InputPaneFactory.createInputPane();
        this.panel.add(inputPane.getPanel());
        initKeybind();
        init = true;
    }

    private void initKeybind() {
        JTextComponent input = inputPane.getInput();
        input.addKeyListener(new KeyHandler());
    }


    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void updateBottomPane(SwingPane swingPane) {
        removeBottomPane();
        addBottomPane(swingPane);
    }

    private void addBottomPane(SwingPane swingPane) {
        this.panel.add(swingPane.getPanel());
    }

    private void removeBottomPane() {
        if (bottomPane != null)
            this.panel.remove(bottomPane.getPanel());
    }

    @Override
    public void updatePane(Pane pane) {
        if (pane == null) {
            removeBottomPane();
            return;
        }
        if (pane instanceof SwingPane) {
            SwingPane swingPane = (SwingPane) pane;
            updateBottomPane(swingPane);
            bottomPane = swingPane;
            applyThemeToPane();
        }
    }

    private void applyToLayout() {
        String layoutColor = currentTheme.findProperty("layout-background-color");
        this.panel.setBackground(Color.decode(layoutColor));
    }

    private void applyThemeToPane() {
        if (bottomPane != null && bottomPane instanceof Themeable) {
            Themeable themeable = bottomPane;
            themeable.apply(currentTheme);
        }
    }

    @Override
    public void apply(LayoutTheme theme) {
        if (theme instanceof LayoutThemeImpl) {
            inputPane.apply(theme);
            currentTheme = (LayoutThemeImpl) theme;
            applyToLayout();
            applyThemeToPane();
        }
    }

    @Override
    public String getName() {
        return "classic_abstract_layout";
    }

    private void check() {
        if (!init) {
            throw new IllegalStateException("This layout "+getName()+" has not been initialized");
        }
    }

    @Override
    public void setDefaultMode() {
        check();
        InputMode inputMode = InputMode.trigger();
        inputMode.updateActionHandler(inputPane);
    }

    @Override
    public void onSetCurrentLayout() {
        inputPane.requireFocus();
    }

    @Override
    public InputPane getInputPane() {
        return inputPane;
    }

    public static ClassicAbstractLayoutImpl instance() {
        if (instance == null) {
            instance = newInstance();
        }
        return instance;
    }


    private static ClassicAbstractLayoutImpl newInstance() {
        ClassicAbstractLayoutImpl abstractClassicLayout = new ClassicAbstractLayoutImpl();
        LayoutFactoryBean.getInstance().initBean(abstractClassicLayout);
        return abstractClassicLayout;
    }
}