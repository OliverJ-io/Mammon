package io.oliverj.mammon.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class TextUtils {
    public static final Component TAB = EconText.of("tab");
    public static final Component ELIPSIS = EconText.of("ellipsis");
    public static final Component UNKNOWN = Component.literal("�").withStyle(Style.EMPTY.withFont(Style.EMPTY.getFont()));

    public static List<Component> splitText(FormattedText text, String regex) {
        ArrayList<Component> textList = new ArrayList<>();

        MutableComponent[] currentText = {Component.empty()};

        text.visit((style, string) -> {
            String[] lines = string.split(regex, -1);

            for (int i = 0; i < lines.length; i++) {
                if (i != 0) {
                    textList.add(currentText[0].copy());
                    currentText[0] = Component.empty();
                }

                currentText[0].append(Component.literal(lines[i]).withStyle(style));
            }

            return Optional.empty();
        }, Style.EMPTY);

        textList.add(currentText[0]);
        return textList;
    }

    public static Component replaceInText(FormattedText text, String regex, Object replacement) {
        return replaceInText(text, regex, replacement, (s, style) -> true, Integer.MAX_VALUE);
    }

    public static Component replaceInText(FormattedText text, String regex, Object replacement, BiPredicate<String, Style> predicate, int times) {
        return replaceInText(text, regex, replacement, predicate, 0, times);
    }

    public static Component replaceInText(FormattedText text, String regex, Object replacement, BiPredicate<String, Style> predicate, int beginIndex, int times) {
        // fix replacement object
        Component replace = replacement instanceof Component c ? c : Component.literal(replacement.toString());
        MutableComponent ret = Component.empty();

        int[] ints = {beginIndex, times};
        text.visit((style, string) -> {
            // test predicate
            if (!predicate.test(string, style)) {
                ret.append(Component.literal(string).withStyle(style));
                return Optional.empty();
            }

            // split
            String[] split = string.split("((?<=" + regex + ")|(?=" + regex + "))");
            for (String s : split) {
                if (!s.matches(regex)) {
                    ret.append(Component.literal(s).withStyle(style));
                    continue;
                }

                if (ints[0] > 0 || ints[1] <= 0) {
                    ret.append(Component.literal(s).withStyle(style));
                } else {
                    ret.append(Component.empty().withStyle(style).append(replace));
                }

                ints[0]--;
                ints[1]--;
            }

            return Optional.empty();
        }, Style.EMPTY);

        return ret;
    }

    public static Component trimToWidthEllipsis(Font font, Component text, int width, Component ellipsis) {
        // return text without changes if it is not larger than width
        if (font.width(text.getVisualOrderText()) <= width)
            return text;

        // add ellipsis
        return addEllipsis(font, text, width, ellipsis);
    }

    public static Component addEllipsis(Font font, FormattedText text, int width, Component ellipsis) {
        // trim with the ellipsis size and return the modified text
        FormattedText trimmed = font.substrByWidth(text, width - font.width(ellipsis));
        return formattedTextToText(trimmed).copy().append(ellipsis);
    }

    public static Component replaceTabs(FormattedText text) {
        return TextUtils.replaceInText(text, "\\t", TAB);
    }

    public static List<FormattedCharSequence> wrapTooltip(FormattedText text, Font font, int mousePos, int screenWidth, int offset) {
        // first split the new line text
        List<Component> splitText = TextUtils.splitText(text, "\n");

        // get the possible tooltip width
        int left = mousePos - offset;
        int right = screenWidth - mousePos - offset;

        // get largest text size
        int largest = getWidth(splitText, font);

        // get the optimal side for warping
        int side = largest <= right ? right : largest <= left ? left : Math.max(left, right);

        // warp the unmodified text
        return wrapText(text, side, font);
    }

    // get the largest text width from a list
    public static int getWidth(List<?> text, Font font) {
        int width = 0;

        for (Object object : text) {
            int w = switch (object) {
                case Component component ->
// instanceof switch case only for java 17 experimental ;-;
                        font.width(component);
                case FormattedCharSequence charSequence -> font.width(charSequence);
                case String s -> font.width(s);
                case null, default -> 0;
            };

            width = Math.max(width, w);
        }
        return width;
    }

    // correctly calculates the height of a list of text componennts
    public static int getHeight(List<?> text, Font font, int lineSpaceing) {
        int lines = text.size();
        return (lines * font.lineHeight) + Math.max((lines-1)*lineSpaceing, 0);
    }

    public static int getHeight(List<?> text, Font font) {
        return getHeight(text, font, 1);
    }

    public static Component replaceStyle(FormattedText text, Style newStyle, Predicate<Style> predicate) {
        MutableComponent ret = Component.empty();
        text.visit((style, string) -> {
            ret.append(Component.literal(string).withStyle(predicate.test(style) ? newStyle.applyTo(style) : style));
            return Optional.empty();
        }, Style.EMPTY);
        return ret;
    }

    public static Component setStyleAtWidth(FormattedText text, int width, Font font, Style newStyle) {
        MutableComponent ret = Component.empty();
        text.visit((style, string) -> {
            MutableComponent current = Component.literal(string).withStyle(style);

            int prevWidth = font.width(ret);
            int currentWidth = font.width(current);
            if (prevWidth <= width && prevWidth + currentWidth > width)
                current.withStyle(newStyle);

            ret.append(current);
            return Optional.empty();
        }, Style.EMPTY);
        return ret;
    }

    public static List<FormattedCharSequence> wrapText(FormattedText text, int width, Font font) {
        List<FormattedCharSequence> warp = new ArrayList<>();
        font.getSplitter().splitLines(text, width, Style.EMPTY, (formattedText, aBoolean) -> warp.add(Language.getInstance().getVisualOrder(formattedText)));
        return warp;
    }

    public static Component charSequenceToText(FormattedCharSequence charSequence) {
        MutableComponent builder = Component.empty();
        StringBuilder buffer = new StringBuilder();
        Style[] lastStyle = new Style[1];

        charSequence.accept((index, style, codePoint) -> {
            if (!style.equals(lastStyle[0])) {
                if (!buffer.isEmpty()) {
                    builder.append(Component.literal(buffer.toString()).withStyle(lastStyle[0]));
                    buffer.setLength(0);
                }
                lastStyle[0] = style;
            }

            buffer.append(Character.toChars(codePoint));
            return true;
        });

        if (!buffer.isEmpty())
            builder.append(Component.literal(buffer.toString()).withStyle(lastStyle[0]));

        return builder;
    }

    public static Component formattedTextToText(FormattedText formattedText) {
        if (formattedText instanceof Component c)
            return c;

        MutableComponent builder = Component.empty();
        formattedText.visit((style, string) -> {
            builder.append(Component.literal(string).withStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return builder;
    }

    public static Component substring(FormattedText text, int beginIndex, int endIndex) {
        StringBuilder counter = new StringBuilder();
        MutableComponent builder = Component.empty();
        text.visit((style, string) -> {
            int index = counter.length();
            int len = string.length();

            if (index <= endIndex && index + len >= beginIndex) {
                int sub = Math.max(beginIndex - index, 0);
                int top = Math.min(endIndex - index, len);
                builder.append(Component.literal(string.substring(sub, top)).withStyle(style));
            }

            counter.append(string);
            return counter.length() > endIndex ? FormattedText.STOP_ITERATION : Optional.empty();
        }, Style.EMPTY);
        return builder;
    }

    public static Component reverse(FormattedText text) {
        MutableComponent[] builder = {Component.empty()};
        text.visit((style, string) -> {
            StringBuilder str = new StringBuilder(string).reverse();
            builder[0] = Component.literal(str.toString()).withStyle(style).append(builder[0]);
            return Optional.empty();
        }, Style.EMPTY);
        return builder[0];
    }

    public static Component trim(FormattedText text) {
        String string = text.getString();
        int start = 0;
        int end = string.length();

        // trim
        while (start < end && string.charAt(start) <= ' ')
            start++;
        while (start < end && string.charAt(end - 1) <= ' ')
            end--;

        // apply trim
        return substring(text, start, end);
    }

    public static List<Component> formatInBounds(FormattedText text, Font font, int maxWidth, boolean wrap) {
        if (maxWidth > 0) {
            if (wrap) {
                List<FormattedCharSequence> warped = wrapText(text, maxWidth, font);
                List<Component> newList = new ArrayList<>();
                for (FormattedCharSequence charSequence : warped)
                    newList.add(charSequenceToText(charSequence));
                return newList;
            } else {
                List<Component> list = splitText(text, "\n");
                List<Component> newList = new ArrayList<>();
                for (Component component : list)
                    newList.add(formattedTextToText(font.substrByWidth(component, maxWidth)));
                return newList;
            }
        } else {
            return splitText(text, "\n");
        }
    }
}
