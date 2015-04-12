package se.kodapan.osm.services.nominatim;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kalle
 * @since 2013-03-27 02:06
 */
public class NominatimQueryBuilderImpl implements NominatimQueryBuilder {
    
    private String baseURL = "http://nominatim.openstreetmap.org/search";
    private HashMap<String, Object> params;
    
    @Override
    public String build(){
        
        StringBuilder url = new StringBuilder();
        url.append(baseURL);
        url.append("?");
        
        params.entrySet().stream().map((entrySet) -> {
            String key = entrySet.getKey();
            Object value = entrySet.getValue();
            url.append(key).append("=");
            url.append(String.valueOf(value));
            return entrySet;
        }).forEach((_item) -> {
            url.append("&");
        });
        String urlString = url.toString();
        return urlString.substring(0,urlString.length() -1);
        
    }
    
    public String getBaseURL() {
        return baseURL;
    }
    
    @Override
    public NominatimQueryBuilderImpl setBaseURL(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }
    
    public String getQuery() {
        return (String) getCustomParam("q");
    }
    
    public void setQuery(String query) throws UnsupportedEncodingException {
        setCustomParam("q", URLEncoder.encode(query, "UTF8").replaceAll("\\+", "%20"));
    }
    
    public String getFormat() {
        return (String) getCustomParam("format");
    }
    
    @Override
    public NominatimQueryBuilderImpl setFormat(String format) {
        setCustomParam("format", format);
        return this;
    }
    
    public void setCountryCodes(List<String> countryCodes) {
        setCustomParam("countrycodes", countryCodes);
    }
    
    @Override
    public NominatimQueryBuilderImpl setCustomParam(String param, Object value) {
        if (params == null) {
            params = new HashMap();
        }
        params.put(param, value);
        return this;
    }
    
    @Override
    public boolean containsCustomParam(String param) {
        return params.containsKey(param);
    }
    
    @Override
    public Object getCustomParam(String param) {
        return params.get(param);
    }
    
}
