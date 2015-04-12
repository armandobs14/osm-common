/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kodapan.osm.services.nominatim;

/**
 *
 * @author armando
 */
public interface NominatimQueryBuilder {
    public NominatimQueryBuilder setBaseURL(String baseURL);
    public NominatimQueryBuilder setFormat(String format);
    public NominatimQueryBuilder setCustomParam(String param, Object value);
    public boolean containsCustomParam(String param);
    public Object getCustomParam(String param);
    public String build();
}
