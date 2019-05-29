package math_encyclopedia.model;

public class Translation implements Comparable {
    String language;
    URL url;

    public Translation(String language, URL url) {
        this.language = language;
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public URL getUrl() {
        return url;
    }

    public String html() {
        return getUrl().html(getLanguage());
    }

    @Override
    public int compareTo(Object o) {
        Translation other = (Translation) o;
        int languagesCompare = getLanguage().compareTo(other.getLanguage());
        if (languagesCompare == 0)
            return getUrl().compareTo(other.getUrl());
        return languagesCompare;
    }
}
