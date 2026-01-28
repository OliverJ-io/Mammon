package io.oliverj.econmod.client.gui;

import net.minecraft.CrashReport;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiPageScreen extends Screen {

    private int pageIndex;

    private final List<Screen> pages = new ArrayList<>();

    public MultiPageScreen(Inventory playerInventory, Component title) {
        super(Component.empty());
    }

    public void addPage(Screen screen) {
        pages.add(screen);
    }

    @Override
    public void init() {
        //pages.forEach(page -> page.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight()));
        pages.get(pageIndex).init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());

        // This works. Don't know why. use instead of init()V
        pages.get(pageIndex).rebuildWidgets();
        //pages.forEach(Screen::rebuildWidgets);
    }

    @Override
    public void render(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        pages.get(pageIndex).render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        return pages.get(pageIndex).mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return pages.get(pageIndex).mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        return pages.get(pageIndex).mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        return pages.get(pageIndex).mouseReleased(event);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        pages.get(pageIndex).mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        return pages.get(pageIndex).keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        return pages.get(pageIndex).keyReleased(event);
    }

    @Override
    public @NonNull Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        return pages.get(pageIndex).getChildAt(mouseX, mouseY);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        return pages.get(pageIndex).charTyped(event);
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        pages.get(pageIndex).renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        pages.get(pageIndex).init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        pages.get(pageIndex).rebuildWidgets();
        super.rebuildWidgets();
    }

    @Override
    public void tick() {
        pages.get(pageIndex).tick();
    }

    @Override
    public void setFocused(boolean focused) {
        pages.get(pageIndex).setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return pages.get(pageIndex).isFocused();
    }

    @Override
    public @Nullable ComponentPath getCurrentFocusPath() {
        return pages.get(pageIndex).getCurrentFocusPath();
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(@NonNull FocusNavigationEvent event) {
        return pages.get(pageIndex).nextFocusPath(event);
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return pages.get(pageIndex).shouldTakeFocusAfterInteraction();
    }

    @Override
    public @NonNull ScreenRectangle getBorderForArrowNavigation(@NonNull ScreenDirection direction) {
        return pages.get(pageIndex).getBorderForArrowNavigation(direction);
    }

    @Override
    public int getTabOrderGroup() {
        return pages.get(pageIndex).getTabOrderGroup();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return pages.get(pageIndex).isMouseOver(mouseX, mouseY);
    }

    @Override
    public void afterMouseMove() {
        pages.get(pageIndex).afterMouseMove();
    }

    @Override
    public void afterMouseAction() {
        pages.get(pageIndex).afterMouseAction();
    }

    @Override
    public void afterKeyboardAction() {
        pages.get(pageIndex).afterKeyboardAction();
    }

    @Override
    public void handleDelayedNarration() {
        pages.get(pageIndex).handleDelayedNarration();
    }

    @Override
    public void triggerImmediateNarration(boolean onlyNarrateNew) {
        pages.get(pageIndex).triggerImmediateNarration(onlyNarrateNew);
    }

    @Override
    public boolean shouldNarrateNavigation() {
        return pages.get(pageIndex).shouldNarrateNavigation();
    }

    @Override
    public void updateNarrationState(@NonNull NarrationElementOutput output) {
        pages.get(pageIndex).updateNarrationState(output);
    }

    @Override
    public void updateNarratedWidget(@NonNull NarrationElementOutput narrationElementOutput) {
        pages.get(pageIndex).updateNarratedWidget(narrationElementOutput);
    }

    @Override
    public @NonNull Component getUsageNarration() {
        return pages.get(pageIndex).getUsageNarration();
    }

    @Override
    public void updateNarratorStatus(boolean narratorEnabled) {
        pages.get(pageIndex).updateNarratorStatus(narratorEnabled);
    }

    @Override
    public boolean showsActiveEffects() {
        return pages.get(pageIndex).showsActiveEffects();
    }

    @Override
    public boolean canInterruptWithAnotherScreen() {
        return pages.get(pageIndex).canInterruptWithAnotherScreen();
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return pages.get(pageIndex).getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        pages.get(pageIndex).setFocused(focused);
    }

    @Override
    public @Nullable Music getBackgroundMusic() {
        return pages.get(pageIndex).getBackgroundMusic();
    }

    @Override
    public @NonNull ScreenRectangle getRectangle() {
        return pages.get(pageIndex).getRectangle();
    }

    @Override
    public @NonNull Font getFont() {
        return pages.get(pageIndex).getFont();
    }

    @Override
    public void onFilesDrop(@NonNull List<Path> packs) {
        pages.get(pageIndex).onFilesDrop(packs);
    }

    @Override
    public void fillCrashDetails(@NonNull CrashReport crashReport) {
        pages.get(pageIndex).fillCrashDetails(crashReport);
    }

    @Override
    public void onClose() {
        pages.get(pageIndex).onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return pages.get(pageIndex).shouldCloseOnEsc();
    }

    @Override
    public void changeFocus(@NonNull ComponentPath path) {
        pages.get(pageIndex).changeFocus(path);
    }

    @Override
    public void clearFocus() {
        pages.get(pageIndex).clearFocus();
    }

    @Override
    public void setInitialFocus(@NonNull GuiEventListener listener) {
        pages.get(pageIndex).setInitialFocus(listener);
    }

    @Override
    public void setInitialFocus() {
        pages.get(pageIndex).setInitialFocus();
    }

    @Override
    public @NonNull Component getNarrationMessage() {
        return pages.get(pageIndex).getNarrationMessage();
    }

    @Override
    public @NonNull Component getTitle() {
        return pages.get(pageIndex).getTitle();
    }
}
