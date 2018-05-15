package io.github.hadixlin.iss;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.command.CommandAdapter;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.maddyhome.idea.vim.extension.VimExtension;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;
import java.util.Set;

/**
 * Created by hadix on 31/03/2017.
 */
public class KeepEnglishInNormalExtension implements VimExtension {

    private static final Set<String> SWITCH_TO_ENGLISH_COMMAND_NAMES
            = ImmutableSet.of("Vim Exit Insert Mode");

    private CommandListener exitInsertModeListener;

    public KeepEnglishInNormalExtension() {
    }

    @NotNull
    @Override
    public String getName() {
        return "keep-english-in-normal";
    }

    @Override
    public void init() {
        if (exitInsertModeListener == null) {
            this.exitInsertModeListener = exitInsertModeListener();
        }
        CommandProcessor.getInstance().addCommandListener(this.exitInsertModeListener);
    }

    @NotNull
    private CommandListener exitInsertModeListener() {
        return new CommandAdapter() {
            @Override
            public void beforeCommandFinished(CommandEvent commandEvent) {
                String commandName = commandEvent.getCommandName();
                if (StringUtils.isBlank(commandName)) {
                    return;
                }
                if (SWITCH_TO_ENGLISH_COMMAND_NAMES.contains(commandName)) {
                    Editor editor = FileEditorManager.getInstance(commandEvent.getProject()).getSelectedTextEditor();
                    Optional.ofNullable(editor.getComponent())
                            .ifPresent(c -> {
                                c.enableInputMethods(false);
                                c.getInputContext().setCompositionEnabled(false);
                            });
                }
            }
        };
    }

    @Override
    public void dispose() {
        if (exitInsertModeListener == null) {
            return;
        }
        CommandProcessor.getInstance().removeCommandListener(exitInsertModeListener);
        exitInsertModeListener = null;
    }

}
