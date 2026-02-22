package dq1.core;

public class WeatherSeasonSystem {
    public enum WeatherType {
        CLEAR, RAIN, STORM, SNOW, FOG, WINDY, HAIL, SLEET, CLOUDY, HEATWAVE, COLDWAVE
    }
    public enum SeasonType {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    public static WeatherType getWeatherForBiome(int biomeId, int day, int seed) {
        int index = Math.abs((biomeId * 17 + day * 23 + seed * 7) % WeatherType.values().length);
        return WeatherType.values()[index];
    }

    public static SeasonType getSeasonForDay(int day) {
        int index = (day / 90) % 4;
        return SeasonType.values()[index];
    }

    public static String getWeatherDescription(WeatherType weather) {
        switch (weather) {
            case CLEAR: return "Clear skies";
            case RAIN: return "Rain showers";
            case STORM: return "Thunderstorm";
            case SNOW: return "Snowfall";
            case FOG: return "Foggy";
            case WINDY: return "Windy";
            case HAIL: return "Hailstorm";
            case SLEET: return "Sleet";
            case CLOUDY: return "Cloudy";
            case HEATWAVE: return "Heatwave";
            case COLDWAVE: return "Cold snap";
            default: return "Unknown weather";
        }
    }

    public static String getSeasonDescription(SeasonType season) {
        switch (season) {
            case SPRING: return "Spring: Blossoms and rain";
            case SUMMER: return "Summer: Warm and sunny";
            case AUTUMN: return "Autumn: Leaves and harvest";
            case WINTER: return "Winter: Cold and snow";
            default: return "Unknown season";
        }
    }
}
