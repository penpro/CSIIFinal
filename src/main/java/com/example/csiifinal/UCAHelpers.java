package com.example.csiifinal;

import java.util.Map;

/**
 * Helper class for the UnitCircleApp that provides utility methods and constant mappings
 * for trigonometric values and angle representations.
 */
public class UCAHelpers {


    /**
     * Returns a string representation of the given degree in terms of π fractions.
     * If the degree does not map to a common π fraction, returns the radian approximation.
     *
     * @param degrees angle in degrees
     * @return string representation in π fraction or decimal radians
     */
    public static String getPiFraction(int degrees) {
        return switch (degrees) {
            case 0 -> "0";
            case 30 -> "π/6";
            case 45 -> "π/4";
            case 60 -> "π/3";
            case 90 -> "π/2";
            case 120 -> "2π/3";
            case 135 -> "3π/4";
            case 150 -> "5π/6";
            case 180 -> "π";
            case 210 -> "7π/6";
            case 225 -> "5π/4";
            case 240 -> "4π/3";
            case 270 -> "3π/2";
            case 300 -> "5π/3";
            case 315 -> "7π/4";
            case 330 -> "11π/6";
            default -> String.format("%.2f", Math.toRadians(degrees));
        };
    }

    /**
     * Map of common angles (in degrees) to their corresponding sine values.
     * Values are represented as simplified square root fractions or decimals.
     */
    public static final Map<Integer, String> SIN_VALUES = Map.ofEntries(
            Map.entry(0, "0"),
            Map.entry(30, "1/2"),
            Map.entry(45, "√2/2"),
            Map.entry(60, "√3/2"),
            Map.entry(90, "1"),
            Map.entry(120, "√3/2"),
            Map.entry(135, "√2/2"),
            Map.entry(150, "1/2"),
            Map.entry(180, "0"),
            Map.entry(210, "-1/2"),
            Map.entry(225, "-√2/2"),
            Map.entry(240, "-√3/2"),
            Map.entry(270, "-1"),
            Map.entry(300, "-√3/2"),
            Map.entry(315, "-√2/2"),
            Map.entry(330, "-1/2")
    );

    /**
     * Map of common angles (in degrees) to their corresponding cosine values.
     * Values are represented as simplified square root fractions or decimals.
     */
    public static final Map<Integer, String> COS_VALUES = Map.ofEntries(
            Map.entry(0, "1"),
            Map.entry(30, "√3/2"),
            Map.entry(45, "√2/2"),
            Map.entry(60, "1/2"),
            Map.entry(90, "0"),
            Map.entry(120, "-1/2"),
            Map.entry(135, "-√2/2"),
            Map.entry(150, "-√3/2"),
            Map.entry(180, "-1"),
            Map.entry(210, "-√3/2"),
            Map.entry(225, "-√2/2"),
            Map.entry(240, "-1/2"),
            Map.entry(270, "0"),
            Map.entry(300, "1/2"),
            Map.entry(315, "√2/2"),
            Map.entry(330, "√3/2")
    );

    /**
     * Map of common angles (in degrees) to their corresponding tangent values.
     * Values include simplified fractions, decimals, and "DNE" where undefined.
     */
    public static final Map<Integer, String> TAN_VALUES = Map.ofEntries(
            Map.entry(0, "0"),
            Map.entry(30, "√3/3"),
            Map.entry(45, "1"),
            Map.entry(60, "√3"),
            Map.entry(90, "DNE Y>0"),
            Map.entry(120, "-√3"),
            Map.entry(135, "-1"),
            Map.entry(150, "-√3/3"),
            Map.entry(180, "0"),
            Map.entry(210, "√3/3"),
            Map.entry(225, "1"),
            Map.entry(240, "√3"),
            Map.entry(270, "DNE Y<0"),
            Map.entry(300, "-√3"),
            Map.entry(315, "-1"),
            Map.entry(330, "-√3/3")
    );

    /** Angles used around the unit circle in degrees. */
    static final int[] ANGLES = {0, 30, 45, 60, 90, 120, 135, 150, 180, 210, 225, 240, 270, 300, 315, 330};

    public static String getLabelForAngle(int angle, UnitCircleApp.AngleDisplayMode mode, boolean includePrefix) {
        return switch (mode) {
            case DEGREES -> angle + "°";
            case RADIANS -> getPiFraction(angle) + (includePrefix ? " rad" : "");
            case SIN -> (includePrefix ? "sin: " : "") + SIN_VALUES.getOrDefault(angle, "?");
            case COS -> (includePrefix ? "cos: " : "") + COS_VALUES.getOrDefault(angle, "?");
            case TAN -> (includePrefix ? "tan: " : "") + TAN_VALUES.getOrDefault(angle, "?");
        };
    }


}


