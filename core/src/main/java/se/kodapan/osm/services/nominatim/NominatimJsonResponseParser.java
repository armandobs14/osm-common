package se.kodapan.osm.services.nominatim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.OsmObject;
import se.kodapan.osm.domain.Relation;
import se.kodapan.osm.domain.Way;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.domain.root.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2013-07-27 21:12
 */
public class NominatimJsonResponseParser {

    private static final Logger log = LoggerFactory.getLogger(NominatimJsonResponseParser.class);

    private Root root = new PojoRoot();

    public List<Result> search(Nominatim nominatim, NominatimQueryBuilder nominatimQueryBuilder) throws Exception {
        String result = nominatim.search(nominatimQueryBuilder.setFormat("json").build());
        return parse(result);
    }

    public List<Result> parse(String nominatimJsonResponse) throws Exception {
        JSONArray jsonResults = null;
        if (nominatimJsonResponse.startsWith("{")) {
            JSONObject result = new JSONObject(nominatimJsonResponse);
            jsonResults = new JSONArray("[" + result.toString() + "]");
        } else {
            jsonResults = new JSONArray(nominatimJsonResponse);
        }
        List<Result> results = new ArrayList<>(jsonResults.length());

        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject jsonResult = (JSONObject) jsonResults.get(i);

            Object osm_type = jsonResult.get("osm_type");
            OsmObject existing;
            long identity = parseJsonDoubleValue(jsonResult, "osm_id").longValue();

            OsmObject object;
            if ("node".equals(osm_type)) {
                Node node = new Node();
                node.setLatitude((parseJsonDoubleValue(jsonResult, "lat")));
                node.setLongitude((parseJsonDoubleValue(jsonResult, "lon")));
                object = node;
                existing = root.getNode(identity);
            } else if ("way".equals(osm_type)) {
                Way way = new Way();
                object = way;
                existing = root.getWay(identity);
            } else if ("relation".equals(osm_type)) {
                Relation relation = new Relation();
                object = relation;
                existing = root.getRelation(identity);
            } else {
                throw new RuntimeException("Unknown osm_type: " + osm_type);
            }

            object.setId(identity);

            if (jsonResult.has("class") && jsonResult.has("type")) {
                object.setTag(
                        (String) jsonResult.get("class"),
                        (String) jsonResult.get("type"));
            }

            Result result = new Result(jsonResult, object);
            
            result.setObject(existing != null ? existing : object);
            results.add(result);
            root.add(object);
        }

        return results;

    }

    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public static class Result {

        private JSONObject jsonObject;
        private Double importance;
        private OsmObject object;

        public Result(JSONObject jsonObject, OsmObject object) {
            this.jsonObject = jsonObject;
            this.object = object;
            this.importance = parseJsonDoubleValue(jsonObject, "importance");
        }

        public Double getImportance() {
            return importance;
        }

        public void setImportance(double importance) {
            this.importance = importance;
        }

        public OsmObject getObject() {
            return object;
        }

        public void setObject(OsmObject object) {
            this.object = object;
        }

        public JSONObject getJsonObject() {
            return jsonObject;
        }

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public String toString() {
            return "Result{"
                    + "importance=" + importance
                    + ", object=" + object
                    + ", jsonObject=" + jsonObject
                    + '}';
        }
    }

    /**
     * @param jsonObject instance of String, Number or null
     * @param key string with the key
     * @return return a double
     * @see Double
     */
    public static Double parseJsonDoubleValue(JSONObject jsonObject, String key) {
        Object value;
        try {
            value = jsonObject.get(key);
        } catch (JSONException e) {
            return null;
        }
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.error("Could not parse double from " + value.toString(), e);
            return null;
        }
    }

}
