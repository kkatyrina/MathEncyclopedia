package math_encyclopedia.model;

public class SearchResult {
    URL    url;
    String body;

    public SearchResult(String title, String url, String fragment) {
        this.url = new URL(url, title);
        this.body = fragment;
    }

    public URL getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }
}
