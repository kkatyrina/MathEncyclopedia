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
        if (url != null)
            html += " href=\"" + url + "\"";
        else
            html += " class=\"undefined\"";
        html += ">" + text + "</a>";
        return html;
    }

    public String html() {
        if (title == null && url == null)
            return "";
        if (title != null)
            return html(title);
        else
            return html(url);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        URL other = (URL) o;
        int res = 0;
        if (title != null && other.title != null)
            res = title.compareTo(other.title);
        if (res == 0 && url != null && other.url != null)
            res = url.compareTo(other.url);
        return res;
    }
}