package math_encyclopedia.model;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.lang.NonNull;

public class URL implements Comparable {
    String url;
    String title;

    public URL(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public URL(String url) {
        this.url = url;
        this.title = null;
    }

    public String html(String text) {
        String html = "<a";
        if (title != null) {
            html += " title=\"" + StringEscapeUtils.escapeHtml4(title) + "\"";
        }
        html += " href=\"" + url + "\"";
        html += ">" + text + "</a>";
        return html;
    }

    public String html() {
        if (title != null)
            return html(title);
        else
            return html(url);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        URL other = (URL) o;
        if (title == null || other.title == null || title.compareTo(other.title) == 0)
            return url.compareTo(other.url);
        return title.compareTo(other.title);
    }
}