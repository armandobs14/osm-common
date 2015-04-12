package se.kodapan.osm.services.nominatim;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import se.kodapan.osm.services.HttpService;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * http://wiki.openstreetmap.org/wiki/Nominatim
 * http://wiki.openstreetmap.org/wiki/Nominatim_usage_policy
 *
 * @author kalle
 * @since 2013-03-26 20:35
 */
public class Nominatim extends HttpService {

    public String search(String url) throws Exception {

        leniency();

        System.out.println(url);

        HttpGet get = new HttpGet(url);
        setUserAgent(get);
        get.setHeader("Content-Encoding", "application/x-www-form-encoded");
        super.open();
        HttpResponse response = getHttpClient().execute(get);

        String string;
        Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF8");
        try {
            string = IOUtils.toString(reader);
        } finally {
            reader.close();
        }
        System.out.println(string);
        return string;

    }

}
