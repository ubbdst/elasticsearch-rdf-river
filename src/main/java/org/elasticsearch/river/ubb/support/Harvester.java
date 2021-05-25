package org.elasticsearch.river.ubb.support;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.tdb.TDBFactory;
import org.elasticsearch.ElasticsearchIllegalStateException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.river.ubb.settings.Defaults;
import org.elasticsearch.river.ubb.settings.RiverUtils;
import org.elasticsearch.river.ubb.utils.FileManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.river.ubb.settings.RiverUtils.*;

/**
 * @author European Environment Agency (EEA) <br>
 * @author Hemed Al Ruwehy
 * <p>
 * Customized to accommodate requests from the University of Bergen Library.<br>
 * by Hemed Ali, 09-03-2015
 */
public class Harvester implements Runnable {

    private final ESLogger logger = Loggers.getLogger(Harvester.class);
    private final Map<String, String> normalizeProp = new HashMap<>();
    private Boolean indexAll = true;
    private String startTime;
    private Set<String> rdfUrls;
    private Set<String> suggestPropList;
    private String rdfEndpoint;
    private String tdbLocation;
    private List<String> rdfQueries;
    private List<String> rdfPaths;
    private String resourceURIFragments;
    private QueryType rdfQueryType;
    private List<String> rdfPropList;
    private Boolean rdfListType = false;
    private Boolean hasList = false;
    private boolean isAutoSuggestionEnabled = true;
    private boolean lowerCaseSuggestInput = true;
    private Boolean removeIllegalCharsForSuggestion = true;
    private Map<String, String> normalizeObj;
    private Map<String, String> normalizeMissing;
    private Boolean willNormalizeProp = false;
    private Boolean willNormalizeObj = false;
    private Boolean addLanguage = false;
    private String language;
    private List<String> uriDescriptionList;
    private Boolean toDescribeURIs = false;
    private Boolean addUriForResource;
    private Boolean hasBlackMap = false;
    private Boolean hasWhiteMap = false;
    private Boolean willNormalizeMissing = false;
    private Map<String, Set<String>> blackMap;
    private Map<String, Set<String>> whiteMap;
    private String syncConditions;
    private String syncTimeProp;
    private Boolean syncOldData;
    private Boolean updateDocuments;
    private long numberOfBulkActions;
    private int maxSuggestInputLength;
    private Client client;
    private String indexName;
    private String typeName;
    private String riverName;
    private String textField;
    private String embedResourceUsingProperty;
    private Boolean closed = false;
    private HashMap<String, String> uriLabelCache;
    private Dataset tdbDataset = null;
    private String queryPath;
    private boolean deleteRiverMappingAfterCreation = false;
    private boolean generateSortLabel = false;
    private long timeStarted = 0;


    /**
     * Sets the {@link Harvester}'s {@link #rdfUrls} parameter
     *
     * @param url - a list of urls
     * @return the {@link Harvester} with the {@link #rdfUrls} parameter set
     */
    public Harvester rdfUrl(String url) {
        url = url.substring(1, url.length() - 1);
        uriLabelCache = new HashMap<>();
        rdfUrls = new HashSet<>(Arrays.asList(url.split(",")));
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfEndpoint} parameter
     *
     * @param endpoint - new endpoint
     * @return the same {@link Harvester} with the {@link #rdfEndpoint}
     * parameter set
     */
    public Harvester rdfEndpoint(String endpoint) {
        rdfEndpoint = endpoint;
        return this;
    }


    /**
     * Sets time started parameter
     */
    public Harvester timeStarted(long timeInMillis) {
        timeStarted = timeInMillis;
        return this;
    }

    /**
     * Sets time started parameter
     */
    public long getTimeStarted() {
        return timeStarted;
    }

    /**
     * Getter for Dataset
     *
     * @return a RDF dataset
     */
    public Dataset getTDBDataset() {
        return tdbDataset;
    }

    /**
     * Setter for TDB dataset
     *
     * @param ds, a RDF dataset
     */
    public void setTDBDataset(Dataset ds) {
        if (ds != null) {
            this.tdbDataset = ds;
        }
    }

    /**
     * Sets the {@link Harvester}'s {@link #tdbLocation} parameter
     *
     * @param pathToTDB
     * @return the same {@link Harvester} with the {@link #tdbLocation} set
     */
    public Harvester rdfTDBLocation(String pathToTDB) {
        tdbLocation = pathToTDB;
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfQueries} parameter
     *
     * @param query - new list of queries
     * @return the same {@link Harvester} with the {@link #rdfQueries}
     * parameter set
     */
    public Harvester rdfQuery(List<String> query) {
        rdfQueries = new ArrayList<>(query);
        return this;
    }

    public Harvester textField(String extractField) {
        this.textField = extractField;
        return this;
    }

    /**
     * Embed resource to another using this property
     *
     * @param embedProperty a property used to embed a resource
     * @return the same {@link Harvester} with the embedResourceUsingProperty parameter set
     */
    public Harvester embedResource(String embedProperty) {
        this.embedResourceUsingProperty = embedProperty;
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfQueryPath(String)} parameter
     *
     * @param pathToSparqlQuery a path where SPARQL query can be readAsUTF8
     * @return the same {@link Harvester} with the {@link #rdfQueryPath(String)} set
     */
    public Harvester rdfQueryPath(String pathToSparqlQuery) {
        if (Strings.hasText(pathToSparqlQuery)) {
            queryPath = pathToSparqlQuery.trim();
        }
        return this;
    }


    public Harvester replaceResourceURI(String fragments) {
        this.resourceURIFragments = fragments;
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfQueryType} parameter
     *
     * @param queryType - the type of any possible query
     * @return the same {@link Harvester} with the {@link #rdfQueryType}
     * parameter set
     */
    public Harvester rdfQueryType(String queryType) {
        try {
            rdfQueryType = QueryType.valueOf(queryType.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Bad query type: {}", queryType);

            /* River process can't continue */
            throw e;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfPropList} parameter
     *
     * @param list - a list of properties names that are either required in
     *             the object description, or undesired, depending on its
     *             {@link #rdfListType}
     * @return the same {@link Harvester} with the {@link #rdfPropList}
     * parameter set
     */
    public Harvester rdfPropList(List<String> list) {
        if (!list.isEmpty()) {
            hasList = true;
            rdfPropList = new ArrayList<>(list);
        }
        return this;
    }

    /**
     * Set auto suggestion list
     **/
    public Harvester setSuggestionList(List<String> suggestProperties) {
        if (!suggestProperties.isEmpty()) {
            isAutoSuggestionEnabled = true;
            suggestPropList = new HashSet<>(suggestProperties);
        }
        return this;
    }

    /**
     * Lower case suggest input
     **/
    public Harvester lowerCaseSuggestInput(boolean flag) {
        this.lowerCaseSuggestInput = flag;
        return this;
    }

    /**
     * Remove illegal characters in the suggestion input.
     * Default to true;
     ***/
    public Harvester removeIllegalCharsForSuggestion(boolean flag) {
        this.removeIllegalCharsForSuggestion = flag;
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #rdfListType} parameter
     *
     * @param listType - a type ("black" or "white") for the
     *                 {@link #rdfPropList} in case it exists
     * @return the same {@link Harvester} with the {@link #rdfListType}
     * parameter set
     * @Observation A blacklist contains properties that should not be
     * indexed with the data while a whitelist contains all the properties
     * that should be indexed with the data.
     */
    public Harvester rdfListType(String listType) {
        if (listType.equals("white")) {
            rdfListType = true;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #addLanguage} parameter.
     *
     * @param rdfAddLanguage - a new value for the parameter
     * @return the same {@link Harvester} with the {@link #addLanguage}
     * parameter set
     * @Observation When "addLanguage" is set on "true", all the languages
     * of the String Literals will be included in the output of a new
     * property, "language".
     */
    public Harvester rdfAddLanguage(Boolean rdfAddLanguage) {
        addLanguage = rdfAddLanguage;
        return this;
    }

    /**
     * Delete river mappings after creation
     */
    public Harvester deleteRiverAfterCreation(boolean flag) {
        if (flag) {
            deleteRiverMappingAfterCreation = true;
        }
        return this;
    }

    /**
     * Decides whether to generate sort label
     */
    public Harvester generateSortLabel(boolean flag) {
        if (flag) {
            generateSortLabel = true;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #language} parameter. The default
     * value is 'en"
     *
     * @param rdfLanguage - new value for the parameter
     * @return the same {@link Harvester} with the {@link #language}
     * parameter set
     */
    public Harvester rdfLanguage(String rdfLanguage) {
        language = StringUtils.deleteWhitespace(rdfLanguage);
        if (!language.isEmpty()) {
            addLanguage = true;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #normalizeProp} parameter.
     * {@link #normalizeProp} contains pairs of property-replacement. The
     * properties are replaced with the given values and if one resource has
     * both properties their values are grouped in a list.
     *
     * @param normalizeProp - new value for the parameter
     * @return the same {@link Harvester} with the {@link #normalizeProp}
     * parameter set
     * @Observation In case there is at least one property, the
     * {@link #willNormalizeProp} parameter is set to true.
     */
    public Harvester rdfNormalizationProp(Map<String, String> normalizeProp) {
        if (normalizeProp != null && !normalizeProp.isEmpty()) {
            this.willNormalizeProp = true;
            //this.normalizeProp = normalizeProp;
            this.normalizeProp.putAll(normalizeProp);
        }
        return this;
    }

    /**
     * Merge context prop with normalize props
     *
     * @param context     a context content.
     * @param transformer an object to transform this context
     * @return this harvester where context builder is already set
     */
    public Harvester rdfContextProp(String context, ContextTransformer transformer) {
        if (context != null && !context.isEmpty()) {
            //Map<String, String> props = contextTransformer.transform(context);
            Map<String, String> props = transformer.transform(context);
            normalizeProp.putAll(props);
            if (!willNormalizeProp) {
                willNormalizeProp = true;
            }
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #normalizeObj} parameter.
     * {@link #normalizeObj} contains pairs of object-replacement. Objects
     * are replaced with given values no matter of the property whose value
     * they represent.
     *
     * @param normalizeObj - new value for the parameter
     * @return the same {@link Harvester} with the {@link #normalizeObj}
     * parameter set
     * @Observation In case there is at least one object to be normalized,
     * the {@link #willNormalizeObj} parameter is set to true
     */
    public Harvester rdfNormalizationObj(Map<String, String> normalizeObj) {
        if (normalizeObj != null && !normalizeObj.isEmpty()) {
            this.willNormalizeObj = true;
            this.normalizeObj = normalizeObj;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #normalizeMissing} parameter.
     * {@link #normalizeMissing} contains pairs of property-value. Missing
     * properties are indexed with the given value.
     *
     * @param normalizeMissing - new value for the parameter
     * @return the same {@link Harvester} with the {@link #normalizeMissing}
     * parameter set
     * @Observation In case there is at least one object to be normalized,
     * the {@link #willNormalizeMissing} parameter is set to true
     */
    public Harvester rdfNormalizationMissing(Map<String, String> normalizeMissing) {
        if (normalizeMissing != null && !normalizeMissing.isEmpty()) {
            this.willNormalizeMissing = true;
            this.normalizeMissing = normalizeMissing;
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #blackMap} parameter. A blackMap
     * contains all the pairs property - list of objects that are not meant
     * to be indexed.
     *
     * @param blackMap - a new value for the parameter
     * @return the same {@link Harvester} with the {@link #blackMap}
     * parameter set
     */
    @SuppressWarnings("unchecked")
    public Harvester rdfBlackMap(Map<String, Object> blackMap) {
        if (blackMap != null && !blackMap.isEmpty()) {
            hasBlackMap = true;
            this.blackMap = new HashMap<String, Set<String>>();
            for (Map.Entry<String, Object> entry : blackMap.entrySet()) {

                this.blackMap.put(
                        entry.getKey(), new HashSet((List<String>) entry.getValue()));
            }
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #whiteMap} parameter. A whiteMap
     * contains all the pairs property - list of objects that are meant to
     * be indexed.
     *
     * @param whiteMap - a new value for the parameter
     * @return the same {@link Harvester} with the {@link #whiteMap}
     * parameter set
     */
    @SuppressWarnings("unchecked")
    public Harvester rdfWhiteMap(Map<String, Object> whiteMap) {
        if (whiteMap != null && !whiteMap.isEmpty()) {
            hasWhiteMap = true;
            this.whiteMap = new HashMap<String, Set<String>>();
            for (Map.Entry<String, Object> entry : whiteMap.entrySet()) {
                this.whiteMap.put(
                        entry.getKey(), new HashSet((List<String>) entry.getValue()));
            }
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #uriDescriptionList} parameter.
     * Whenever {@link #uriDescriptionList} is set, all the objects
     * represented by URIs are replaced with the resource's label. The label
     * is the first of the properties in the given list, for which the
     * resource has an object.
     *
     * @param uriList - a new value for the parameter
     * @return the same {@link Harvester} with the
     * {@link #uriDescriptionList} parameter set
     * @Observation If the list is not empty, the {@link #toDescribeURIs}
     * property is set to true
     */
    public Harvester rdfURIDescription(List<String> uriList) {
        if (!uriList.isEmpty()) {
            toDescribeURIs = true;
            uriDescriptionList = new ArrayList<>(uriList);
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #uriDescriptionList} parameter.
     * When it is set to true a new property is added to each resource:
     * <http://www.w3.org/1999/02/22-rdf-syntax-ns#about>, having the value
     * equal to the resource's URI.
     *
     * @param rdfAddUriForResource - a new value for the parameter
     * @return the same {@link Harvester} with the
     * {@link #addUriForResource} parameter set
     */
    public Harvester rdfAddUriForResource(Boolean rdfAddUriForResource) {
        this.addUriForResource = rdfAddUriForResource;
        return this;
    }

    /**
     * public Harvester rdfURIDescription(String uriList) { uriList =
     * uriList.substring(1, uriList.length() - 1); if(!uriList.isEmpty())
     * toDescribeURIs = true; uriDescriptionList =
     * Arrays.asList(uriList.split(",")); return this;
     }*
     */

    /**
     * Sets the {@link Harvester}'s {@link #syncConditions} parameter. It
     * represents the sync query's additional conditions for indexing. These
     * conditions are added to the time filter.
     *
     * @param syncCond - a new value for the parameter
     * @return the same {@link Harvester} with the {@link #syncConditions}
     * parameter set
     */
    public Harvester rdfSyncConditions(String syncCond) {
        this.syncConditions = syncCond;
        if (!syncCond.isEmpty()) {
            this.syncConditions += " . ";
        }
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #syncTimeProp} parameter. It
     * represents the sync query's time parameter used when filtering the
     * endpoint's last updates.
     *
     * @param syncTimeProp - a new value for the parameter
     * @return the same {@link Harvester} with the {@link #syncTimeProp}
     * parameter set
     */
    public Harvester rdfSyncTimeProp(String syncTimeProp) {
        this.syncTimeProp = syncTimeProp;
        return this;
    }

    /**
     * Sets the {@link Harvester}'s {@link #syncOldData} parameter. When
     * this parameter is set to true, the endpoint will be queried again
     * without the {@link #syncConditions} to update existing resources that
     * were changed. THe default value is true
     *
     * @param syncOldData - a new value for the parameter return the same
     *                    {@link Harvester} with the {@link #syncOldData} parameter set
     */
    public Harvester rdfSyncOldData(Boolean syncOldData) {
        this.syncOldData = syncOldData;
        return this;
    }

    /**
     * This flag is set if you want to update the documents instead of
     * indexing.
     *
     * @param updateDocs
     * @return
     */
    public Harvester rdfUpdateDocuments(Boolean updateDocs) {
        this.updateDocuments = updateDocs;
        return this;
    }

    /**
     * @param bulkActions
     * @return this object with numberOfBulkActions parameter set
     */
    public Harvester rdfNumberOfBulkActions(long bulkActions) {
        this.numberOfBulkActions = bulkActions;
        return this;
    }

    public Harvester maxSuggestInputLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Expected positive number for " +
                    "maxSuggestInputLength but found [ " + length + "]");
        }
        this.maxSuggestInputLength = length;
        return this;
    }

    public Harvester client(Client client) {
        this.client = client;
        return this;
    }

    public Harvester index(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public Harvester type(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public Harvester riverName(String riverName) {
        this.riverName = riverName;
        return this;
    }

    public Harvester rdfIndexType(String indexType) {
        if (indexType.equals("sync")) {
            this.indexAll = false;
        }
        return this;
    }

    public Harvester rdfStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public void log(String message) {
        logger.info(message);
    }

    public void setClose(Boolean value) {
        this.closed = value;
    }

    private void setLastUpdate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        try {
            bulkRequest.add(client.prepareIndex(indexName, "stats", "1")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("last_update", sdf.format(date))
                            .endObject()));
        } catch (IOException ioe) {
            logger.error("Could not add the stats to ES. {}",
                    ioe.getLocalizedMessage());
        }
        bulkRequest.execute().actionGet();
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        boolean success;

        if (indexAll) {
            success = runIndexAll();
        } else {
            success = runSync();
        }
        if (success) {
            setLastUpdate(new Date(currentTime));
        }
        //Delete river if specified by a user
        if (deleteRiverMappingAfterCreation) {
            try {
                client.admin().indices()
                        .prepareDeleteMapping("_river")
                        .setType(riverName)
                        .execute()
                        .actionGet();
            } catch (ElasticsearchIllegalStateException e) {
                //Whenever river is deleted, IllegalStateException is thrown. I think this is a
                //bug from Elasticsearch itself. I am catching it to reduce noise in the cluster
                logger.info("Deleted mappings for river [{}]", riverName);
            }
        }
    }

    public boolean runSync() {
        logger.info("Starting RDF synchronizer: from [{}], endpoint [{}], "
                        + "index name [{}], type name [{}]",
                startTime, rdfEndpoint, indexName, typeName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date lastUpdate = new Date(System.currentTimeMillis());

        /**
         * Synchronize with the endpoint
         */
        if (startTime.isEmpty()) {
            GetResponse response = client
                    .prepareGet(indexName, "stats", "1")
                    .setFields("last_update")
                    .execute()
                    .actionGet();
            startTime = (String) response.getField("last_update").getValue();
        }

        try {
            lastUpdate = sdf.parse(startTime);
        } catch (Exception e) {
            logger.error("Could not parse time. [{}]", e.getLocalizedMessage());
        }

        boolean success = sync();

        closed = true;
        logger.info("Ended synchronization from [{}], for endpoint [{}],"
                        + "index name [{}], type name [{}] with status {}",
                lastUpdate, rdfEndpoint, indexName, typeName,
                success ? "Success" : "Failure");
        return success;
    }

    /**
     * Get a set of unique queryObjName returned from a select query
     * <p>
     * Used to retrieve sets of modified objects used in sync
     *
     * @param rdfQuery     query to execute
     * @param queryObjName name of the object returned
     * @return set of values for queryObjectName in the rdfQuery result
     */
    HashSet<String> executeSyncQuery(String rdfQuery, String queryObjName) {
        HashSet<String> rdfUrls = new HashSet<String>();

        Query query;
        try {
            query = QueryFactory.create(rdfQuery);
        } catch (QueryParseException qpe) {
            logger.warn(
                    "Could not parse [{}]. Please provide a relevant quey. {}",
                    rdfQuery, qpe.getLocalizedMessage());
            return null;
        }

        QueryExecution qexec = QueryExecutionFactory.sparqlService(
                rdfEndpoint, query);
        try {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                try {
                    String value = sol.getResource(queryObjName).toString();
                    rdfUrls.add(value);
                } catch (NoSuchElementException nsee) {
                    logger.error(
                            "Encountered a NoSuchElementException: "
                                    + nsee.getLocalizedMessage());
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error(
                    "Encountered a [{}] while querying the endpoint for sync",
                    e.getLocalizedMessage());
            return null;
        } finally {
            qexec.close();
        }

        return rdfUrls;
    }

    /**
     * Build a query returning all triples in which members of uris are the
     * subjects of the triplets.
     * <p>
     * If toDescribeURIs is true the query will automatically add logic to
     * retrieve the labels directly from the SPARQL endpoint.
     *
     * @param uris URIs for queried resources
     * @return a CONSTRUCT query string
     */
    private String getSyncQueryStr(Iterable<String> uris) {
        StringBuilder uriSetStrBuilder = new StringBuilder();
        String delimiter = "";

        uriSetStrBuilder.append("(");
        for (String uri : uris) {
            uriSetStrBuilder.append(delimiter).append(String.format("<%s>", uri));
            delimiter = ", ";
        }
        uriSetStrBuilder.append(")");

        String uriSet = uriSetStrBuilder.toString();

        /* Get base triplets having any element from uris as subject */
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CONSTRUCT { ?s ?p ?o } WHERE {")
                .append("{?s ?p ?o")
                .append(String.format(" . FILTER (?s in %s )", uriSet));

        /* Perform uri label resolution only if desired */
        if (!toDescribeURIs) {
            queryBuilder.append("}}");
            return queryBuilder.toString();
        }

        /* Filter out properties having a label */
        int index = 0;
        for (String prop : uriDescriptionList) {
            index++;
            String filterTemplate = " . OPTIONAL { ?o <%s> ?o%d } "
                    + " . FILTER(!BOUND(?o%d))";
            queryBuilder.append(String.format(filterTemplate, prop, index, index));
        }
        queryBuilder.append("}");

        /* Add labels for filtered out properties */
        for (String prop : uriDescriptionList) {
            /* Resolve ?o as being the <prop> for resource ?o1 */
            String partQueryTemplate = " UNION "
                    + "{ ?s ?p ?o1"
                    + " . FILTER (?s in %s)"
                    + " . ?o1 <%s> ?o }";
            queryBuilder.append(String.format(partQueryTemplate, uriSet, prop));
        }

        queryBuilder.append("}");
        return queryBuilder.toString();

    }

    /**
     * Starts a harvester with predefined queries to synchronize with the
     * changes from the SPARQL endpoint
     *
     * @return
     */
    public boolean sync() {
        logger.info("Sync resources newer than {}", startTime);
        String rdfQueryTemplate
                = "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
                + "SELECT ?resource WHERE { "
                + "?resource <%s> ?time . %s"
                + " FILTER (?time > xsd:dateTime(\"%s\")) }";

        String queryStr = String.format(rdfQueryTemplate, syncTimeProp,
                syncConditions, startTime);
        rdfUrls = executeSyncQuery(queryStr, "resource");

        if (rdfUrls == null) {
            logger.error("Errors occurred during sync procedure. Aborting!");
            return false;
        }

        /**
         * If desired, query for old data that has the sync conditions
         * modified
         *
         * This option is useful in the case in which the application
         * indexes resources that match some conditions. In this case,
         * if they are modified and no longer match the initial
         * conditions, they will not be synchronized. When syncOldData
         * is True, the modified resources that no longer match the
         * conditions are deleted.
         *
         *
         */
        int deleted = 0;
        int count = 0;
        if (this.syncOldData) {
            rdfQueryTemplate = "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
                    + "SELECT ?resource WHERE { "
                    + "?resource <%s> ?time ."
                    + " FILTER (?time > xsd:dateTime(\"%s\")) }";
            queryStr = String.format(rdfQueryTemplate, syncTimeProp, startTime);

            HashSet<String> notMatchingUrls = executeSyncQuery(queryStr, "resource");

            if (notMatchingUrls == null) {
                logger.error("Errors occurred during modified content sync query. Aborting!");
                return false;
            }

            notMatchingUrls.removeAll(rdfUrls);

            for (String uri : notMatchingUrls) {
                DeleteRequestBuilder request = client.prepareDelete(
                        indexName, typeName, uri);
                DeleteResponse response = request.execute().actionGet();

                if (response.isFound()) {
                    deleted++;
                    logger.info("Deleted resource not matching sync properties: {}", uri);
                }
            }
        }

        /* Prepare a series of bulk uris to be described so we can make
         * a smaller number of calls to the SPARQL endpoint. */
        ArrayList<ArrayList<String>> bulks = new ArrayList<ArrayList<String>>();
        ArrayList<String> currentBulk = new ArrayList<String>();

        for (String uri : rdfUrls) {
            currentBulk.add(uri);

            if (currentBulk.size() == Defaults.DEFAULT_BULK_REQ) {
                bulks.add(currentBulk);
                currentBulk = new ArrayList<String>();
            }
        }

        if (currentBulk.size() > 0) {
            bulks.add(currentBulk);
        }

        /* Execute RDF queries for the resources in each bulk */
        for (ArrayList<String> bulk : bulks) {
            String syncQuery = getSyncQueryStr(bulk);

            try {
                Query query = QueryFactory.create(syncQuery);
                QueryExecution qexec = QueryExecutionFactory.sparqlService(
                        rdfEndpoint, query);
                try {
                    Model constructModel = ModelFactory.createDefaultModel();
                    qexec.execConstruct(constructModel);
                    BulkRequestBuilder bulkRequest = client.prepareBulk();

                    /**
                     * When adding the model to ES save old
                     * state of toDescribeURIs as the query
                     * already returned the correct labels.
                     */
                    boolean oldToDescribeURIs = toDescribeURIs;
                    toDescribeURIs = false;
                    addModelToElasticsearch(constructModel, bulkRequest);
                    toDescribeURIs = oldToDescribeURIs;
                    count += bulk.size();
                } catch (Exception e) {
                    logger.error(
                            "Error while querying for modified content. {}",
                            e.getLocalizedMessage());
                    return false;
                } finally {
                    qexec.close();
                }
            } catch (QueryParseException qpe) {
                logger.warn(
                        "Could not parse Sync query. Please provide a relevant query. {}",
                        qpe.getLocalizedMessage());
                return false;
            }

        }
        logger.info("Finished synchronisation: Deleted {}, Updated {}/{}",
                deleted, count, rdfUrls.size());
        return true;
    }

    /**
     * Starts the harvester for queries and/or URLs
     *
     * @return
     */
    public boolean runIndexAll() {
        /*logger.info(
                "Starting RDF harvester: endpoint [{}], TDB [{}] queries [{}],"
                + "URLs [{}], index name [{}], typeName [{}]",
                rdfEndpoint, tdbLocation, rdfQueries, rdfUrls, indexName, typeName);
         */
        while (true) {
            if (this.closed) {
                logger.info("Ended the harvest for river [{}] on index [{}] and type [{}]",
                        riverName, indexName, typeName);
                return true;
            }

            /*
             * Harvest from a SPARQL endpoint
             */
            if (!rdfEndpoint.trim().isEmpty()) {
                harvestFromEndpoint();
            }

            /*
             * Harvest from TDB
             */
            if (!tdbLocation.trim().isEmpty()) {
                harvestFromTDB();
            }

            /*
             * Harvest from RDF dumps
             */
            harvestFromDumps();

            closed = true;
        }
    }

    /**
     * Query SPARQL endpoint with a CONSTRUCT query
     *
     * @param qexec QueryExecution encapsulating the query
     * @return model retrieved by querying the endpoint
     */
    private Model getConstructModel(QueryExecution qexec) {
        return qexec.execConstruct(ModelFactory.createDefaultModel());
    }

    /**
     * Query SPARQL endpoint with a DESCRIBE query
     *
     * @param qexec QueryExecution encapsulating the query
     * @return model retrieved by querying the endpoint
     */
    private Model getDescribeModel(QueryExecution qexec) {
        return qexec.execDescribe(ModelFactory.createDefaultModel());
    }

    /**
     * Query SPARQL endpoint with a SELECT query
     *
     * @param qexec QueryExecution encapsulating the query
     * @return model retrieved by querying the endpoint
     */
    private Model getSelectModel(QueryExecution qexec) {
        Model model = ModelFactory.createDefaultModel();
        Graph graph = model.getGraph();
        ResultSet results = qexec.execSelect();

        while (results.hasNext()) {
            QuerySolution sol = results.next();
            String subject;
            String predicate;
            RDFNode object;

            try {
                subject = sol.getResource("s").toString();
                predicate = sol.getResource("p").toString();
                object = sol.get("o");
            } catch (NoSuchElementException nsee) {
                logger.error("SELECT query does not return a (?s ?p ?o) Triple");
                continue;
            }

            Node objNode;
            if (object.isLiteral()) {
                Literal obj = object.asLiteral();
                objNode = NodeFactory.createLiteral(obj.getString(), obj.getDatatype());
            } else {
                objNode = NodeFactory.createLiteral(object.toString());
            }

            graph.add(new Triple(
                    NodeFactory.createURI(subject),
                    NodeFactory.createURI(predicate),
                    objNode));
        }

        return model;
    }

    /**
     * Query the SPARQL endpoint with a specified QueryExecution and return
     * the model
     *
     * @param qexec QueryExecution encapsulating the query
     * @return model retrieved by querying the endpoint
     */
    private Model getModel(QueryExecution qexec) {
        switch (rdfQueryType) {
            case CONSTRUCT:
                return getConstructModel(qexec);
            case DESCRIBE:
                return getDescribeModel(qexec);
            case SELECT:
                return getSelectModel(qexec);
        }
        return null;
    }

    /**
     * Add data to ES given a query execution service
     *
     * @param qexec query execution service
     */
    private void harvest(QueryExecution qexec) throws IOException {
        boolean retry;
        int countRetry = 0;
        int totalRetry = 5;
        do {
            retry = false;
            try {
                Model model = getModel(qexec);
                addModelToElasticsearch(model, client.prepareBulk());
            } catch (QueryExceptionHTTP httpe) {
                if (httpe.getResponseCode() >= 500) {
                    retry = true;
                    countRetry++;
                    logger.error("Encountered internal server error while harvesting. " +
                            "Retrying... {}", countRetry);
                } else {
                    throw httpe;
                }
                if (countRetry > totalRetry) {
                    //if we have reached maximum retries, exit
                    break;
                }
            } catch (Exception e) {
                logger.error("Exception occurred while harvesting with details:  [{}] ",
                        e.getLocalizedMessage());
                e.printStackTrace();
            }
        } while (retry);
    }

    /**
     * Queries the {@link #rdfEndpoint(String)} with each of the
     * {@link #rdfQueries} and harvests the results of the query.
     */
    private void harvestFromEndpoint() {
        //Harvesting using a given SPARQL query path
        if (Strings.hasText(queryPath)) {
            logger.info("Harvesting from endpoint [{}] using query path [{}] for river [{}] on index " +
                    "[{}] and type [{}]", rdfEndpoint, queryPath, riverName, indexName, typeName);

            Query queryFromPath = null;
            try {
                queryFromPath = QueryFactory.read(queryPath);
            } catch (QueryParseException qpe) {
                logger.error("Could not parse [{}]. Please provide a relevant query. {}", queryPath, qpe);
            }

            if (queryFromPath != null) {
                try (QueryExecution qE = QueryExecutionFactory.sparqlService(rdfEndpoint, queryFromPath)) {
                    harvest(qE);
                } catch (IOException e) {
                    logger.error("Error while harvesting from {}", queryPath, e.getLocalizedMessage());
                }
            }
        }

        //Harvesting using list of RDF queries
        if (!rdfQueries.isEmpty()) {
            Query query;
            for (String rdfQuery : rdfQueries) {
                logger.info("Harvesting from endpoint [{}] for river [{}] on index [{}] and type [{}] using provided queries",
                        rdfEndpoint, riverName, indexName, typeName);
                try {
                    query = QueryFactory.create(rdfQuery);
                } catch (QueryParseException qpe) {
                    logger.error("Could not parse [{}]. Please provide a relevant query. {}", rdfQuery, qpe);
                    continue;
                }
                try (QueryExecution qexec = QueryExecutionFactory.sparqlService(rdfEndpoint, query)) {
                    harvest(qexec);
                } catch (Exception e) {
                    logger.error("Exception [{}] occurred while harvesting", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes describe query for a given resource against a TDB dataset or endpoint
     */
    private Model describe(Resource resource) {
        String describeQuery = "DESCRIBE <" + resource.toString() + ">";

        if (tdbDataset != null) { //Try TDB
            try (QueryExecution qE = QueryExecutionFactory.create(describeQuery, tdbDataset)) {
                return getDescribeModel(qE);
            }
        } else if (Strings.hasText(rdfEndpoint)) { //Try endpoint
            try (QueryExecution qE = QueryExecutionFactory.sparqlService(rdfEndpoint, describeQuery)) {
                return getDescribeModel(qE);
            }
        }
        return null;  //if all are not available, return null
    }

    /**
     * Harvest from TDB using queries specified from {@link #rdfQueries
     * and/or path specified in {@link #queryPath}.
     */
    private void harvestFromTDB() {
        if (!rdfQueries.isEmpty()) { //Harvesting from a list of RDF Queries
            Query queryFromList;
            for (String rdfQuery : rdfQueries) {
                logger.info("Harvesting from TDB store [{}] for river [{}] on index [{}] and type [{}]",
                        tdbLocation, riverName, indexName, typeName);

                try {
                    queryFromList = QueryFactory.create(rdfQuery);
                } catch (QueryParseException qpe) {
                    logger.error(
                            "Could not parse [{}]. Please provide a relevant query. {}",
                            rdfQuery, qpe);
                    continue;
                }
                if (queryFromList != null) {
                    //Do harvesting
                    harvest(createOrConnect(tdbLocation), queryFromList);
                }

            }
        }
        if (Strings.hasText(queryPath)) {//harvesting from file path
            logger.info("Harvesting from TDB [{}] using query path [{}] for river [{}] " +
                    "on index [{}] and type [{}]", tdbLocation, queryPath, riverName, indexName, typeName);
            Query queryFromPath = null;
            try {
                queryFromPath = QueryFactory.read(queryPath);
            } catch (QueryParseException qpe) {
                logger.error("Could not parse [{}]. Please provide a relevant query. {}", queryPath, qpe);
            }
            if (queryFromPath != null) {
                //Do harvesting
                harvest(createOrConnect(tdbLocation), queryFromPath);
            }
        }

    }


    /**
     * Creates or connects to the TDB-backend
     *
     * @param path location to a TDB dataset
     * @return dataset or null if cannot create or connect
     */
    private Dataset createOrConnect(String path) {
        Dataset dataset = null;
        try {
            //Create or connect to the TDB-backend
            dataset = TDBFactory.createDataset(path);
        } catch (Exception e) {
            logger.error("Cannot create or connect to dataset for path: [{}]", path);
            throw e;
        } finally {
            // Store this TDB dataset such that we can use it afterwards
            // e.g for getting labels
            setTDBDataset(dataset);
        }
        return dataset;
    }

    /**
     * Harvest data using a given TDB dataset
     *
     * @param query   a given query
     * @param dataset a given dataset to query against
     */
    private void harvest(Dataset dataset, Query query) {
        Objects.requireNonNull(dataset, "Dataset cannot be null");
        //Begin READ transaction
        dataset.begin(ReadWrite.READ);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
            //Harvest and send model to Elasticsearch
            Model model = getModel(qexec);
            addModelToElasticsearch(model, client.prepareBulk());
        } catch (Exception e) {
            logger.error("Exception occurred while harvesting data using TDB [{}] ", e.getLocalizedMessage());
            e.printStackTrace();

        } finally {
            //Release resources
            dataset.end();
            dataset.close();
        }
    }

    /**
     * Harvests all the triplets from each URI in the @rdfUrls list
     */
    private void harvestFromDumps() {
        for (String url : rdfUrls) {
            if (url.isEmpty()) {
                continue;
            }
            logger.info("Harvesting url [{}]", url);
            Model model = ModelFactory.createDefaultModel();
            try {
                RDFDataMgr.read(model, url.trim(), RDFLanguages.RDFXML);
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                addModelToElasticsearch(model, bulkRequest);
            } catch (RiotException re) {
                logger.error("Illegal xml character [{}]", re.getLocalizedMessage());
            } catch (Exception e) {
                logger.error("Exception when harvesting url: {}. Details: {}",
                        url, e.getLocalizedMessage());
            }
        }
    }

    /**
     * Get JSON map for a given resource by applying the river settings
     *
     * @param rs         resource being processed
     * @param properties properties to be indexed
     * @param model      model returned by the indexing query
     * @return map of properties to be indexed for res
     */
    private Map<String, Object> getJsonMap(Resource rs, Set<Property> properties, Model model) {
        Map<String, Object> jsonMap = new HashMap<>();
        List<String> results = new ArrayList<>();
        Set<String> suggestInputs = new HashSet<>();
        Set<String> rdfLanguages = new HashSet<>();
        if (addUriForResource) {
            results.add(RiverUtils.replaceResourceURI(rs.toString(), resourceURIFragments));
            String normalizedProperty = Defaults.DEFAULT_RESOURCE_URI;

            // If a property is defined in the normProp list, then use
            // the normalized (shorten) property.
            if (willNormalizeProp && normalizeProp.containsKey(Defaults.DEFAULT_RESOURCE_URI)) {
                normalizedProperty = normalizeProp.get(Defaults.DEFAULT_RESOURCE_URI);
            }
            jsonMap.put(normalizedProperty, results);
        }
        for (Property prop : properties) {
            NodeIterator niter = model.listObjectsOfProperty(rs, prop);
            String property = prop.toString();
            results = new ArrayList<>();

            String lang;
            String currentValue;
            String suggestValue;
            List<Map<String, Object>> innerObject = new ArrayList<>();
            while (niter.hasNext()) {
                RDFNode node = niter.next();
                currentValue = getStringForResult(rs, node);

                //If a literal contains empty value, skip and do not index
                if (currentValue.isEmpty()) {
                    continue;
                }

                //If we have to generate label sort
                if (generateSortLabel) {
                    if (node.isLiteral()) {
                        String sortLabel = RiverUtils.constructLabelSort(property, currentValue);
                        if (Strings.hasText(sortLabel)) {
                            jsonMap.put(Defaults.SORT_LABEL_NAME, sortLabel);
                        }
                    }
                }

                //Embed one resource to another using the given property
                if (node.isResource() && property.equals(embedResourceUsingProperty)) {
                    if (logger.isDebugEnabled()) {
                        logger.info("Embedding resource " + node.asResource().getURI() + " to " + rs);
                    }
                    Resource eResource = node.asResource();
                    Model eModel = describe(eResource);
                    Set<Property> eProperties = getProperties(eModel.listStatements());
                    boolean wasSuggestOn = false;
                    if (isAutoSuggestionEnabled) {//switch off suggestion for embedded document
                        wasSuggestOn = true;
                        isAutoSuggestionEnabled = false;
                    }
                    //Recursive call to embed another resource
                    jsonMap.put("_embedded", convertSingleValueListToString(getJsonMap(eResource, eProperties, eModel)));
                    if (wasSuggestOn) {//Turn back on the suggestion
                        isAutoSuggestionEnabled = true;
                    }
                }

                // Read and index contents of a given URL
                if (Strings.hasText(textField) && property.equals(textField)) {
                    jsonMap.put(Defaults.TEXT_CONTENT_FIELD, FileManager.readUrlContent(currentValue));
                }

                if (addLanguage) {
                    if (node.isLiteral()) {
                        lang = node.asLiteral().getLanguage();
                        if (!lang.isEmpty()) {
                            rdfLanguages.add(lang);
                        }
                    }
                }

                // Construct inner map if value contains special chars
                if (RiverUtils.isInnerObject(currentValue)) {
                    Map<String, Object> innerMap = constructInnerMap(currentValue);
                    innerObject.add(innerMap);
                }

                //Add values to suggest field for auto suggestion.
                if (isAutoSuggestionEnabled) {
                    //Filter the value, such that it should not contain weird characters
                    if (!currentValue.startsWith("http")
                            && !isInnerObject(currentValue)
                            && currentValue.length() <= maxSuggestInputLength
                            && !currentValue.equalsIgnoreCase("true")
                            && !currentValue.equalsIgnoreCase("false")) {

                        suggestValue = currentValue;
                        if (removeIllegalCharsForSuggestion) {
                            //Replace possible illegal characters with empty space.
                            //These characters have special meaning in Elasticsearch,
                            //so we remove them in a suggestion list.
                            suggestValue = RiverUtils.removeSpecialCharsForAutoSuggest(suggestValue);
                        }
                        //Add value to the list
                        if (Strings.hasText(suggestValue) && Character.isLetter(suggestValue.charAt(0))) {
                            suggestInputs.add(this.lowerCaseSuggestInput ?
                                    suggestValue.toLowerCase(Locale.ROOT) : suggestValue);
                        }
                    }
                }

                // If either whiteMap does contains shortValue
                // or blackMap contains the value
                // skip adding it to the index
                boolean whiteMapCond = hasWhiteMap
                        && whiteMap.containsKey(property)
                        && !whiteMap.get(property).contains(currentValue);
                boolean blackMapCond = hasBlackMap
                        && blackMap.containsKey(property)
                        && blackMap.get(property).contains(currentValue);

                if (whiteMapCond || blackMapCond) {
                    continue;
                }

                if (willNormalizeObj && normalizeObj.containsKey(currentValue)) {
                    results.add(normalizeObj.get(currentValue));
                } else {
                    results.add(currentValue);
                }

            }

            // Do not index empty properties
            if (results.isEmpty()) {
                continue;
            }

            // Add inner object for a given property
            if (!innerObject.isEmpty()) {
                if (normalizeProp.containsKey(property)) {
                    property = normalizeProp.get(property);
                }
                if (innerObject.size() == 1) { // list with only one object
                    jsonMap.put(property, innerObject.get(0));
                } else { // list with more than one objects
                    jsonMap.put(property, innerObject);
                }
                // Skip indexing this property since we already have constructed
                // inner object with this property as a key
                continue;
            }

            //Normalize properties
            if (willNormalizeProp && normalizeProp.containsKey(property)) {
                property = normalizeProp.get(property);
                if (jsonMap.containsKey(property)) {// if we have more than one properties
                    Object values = jsonMap.get(property);
                    if (values instanceof List) {
                        results.addAll((List) values);
                        jsonMap.put(property, results);
                    }
                } else {
                    jsonMap.put(property, results);
                }
            } else {
                jsonMap.put(property, results);
            }
        }

        if (addLanguage) {
            if (rdfLanguages.isEmpty() && !language.isEmpty()) {
                rdfLanguages.add(language);
            }
            if (!rdfLanguages.isEmpty()) {
                ArrayList<String> langs = new ArrayList<>(rdfLanguages);
                jsonMap.put("language", langs.size() == 1 ? langs.get(0) : langs);
            }
        }

        if (willNormalizeMissing) {
            for (Map.Entry<String, String> it : normalizeMissing.entrySet()) {
                if (!jsonMap.containsKey(it.getKey())) {
                    ArrayList<String> res = new ArrayList<String>();
                    res.add(it.getValue());
                    jsonMap.put(it.getKey(), res);
                }
            }
        }

        //Insert suggest filed in every document
        if (suggestInputs.size() > 0) {
            Map<String, Object> suggestMap = new HashMap<>();
            suggestMap.put(Defaults.SUGGESTION_INPUT_FIELD, suggestInputs);
            jsonMap.put(Defaults.SUGGESTION_FIELD, suggestMap);
        }


        return jsonMap;
    }


    /**
     * Gets all properties that match our criteria
     *
     * @param iter statement iterator
     * @return set of properties
     */
    private Set<Property> getProperties(StmtIterator iter) {
        Set<Property> properties = new HashSet<>();
        while (iter.hasNext()) {
            Statement st = iter.nextStatement();
            Property prop = st.getPredicate();
            String property = prop.toString();

            if (!hasList
                    || (rdfListType && rdfPropList.contains(property))
                    || (!rdfListType && !rdfPropList.contains(property))
                    || (willNormalizeProp && normalizeProp.containsKey(property))) {
                properties.add(prop);
            }
        }
        return properties;
    }


    /**
     * Index or update all the resources in a Jena Model to ES Note: Update
     * works if the user has specified the flag <tt>updateDocuments</tt> to true in
     * the river settings. It is set to false by default. By doing this, you
     * can partial update documents without full reindexing.
     *
     * @param model       the model to index
     * @param bulkRequest a BulkRequestBuilder
     */
    private void addModelToElasticsearch(Model model, BulkRequestBuilder bulkRequest) {
        logger.info("Indexing into Elasticsearch for river [{}] on index [{}] and type [{}]",
                riverName, indexName, typeName);
        //Abort if model is empty
        if (Objects.isNull(model) || model.isEmpty()) {
            logger.warn("Encountered empty model for river [{}]. Aborting ...", riverName);
            return;
        }
        long startTime = System.currentTimeMillis();
        long bulkLength = 0;
        Set<Property> properties = getProperties(model.listStatements());
        ResIterator resIterator = model.listSubjects();
        while (resIterator.hasNext()) {
            Resource rs = resIterator.nextResource();
            Map<String, Object> jsonMap = getJsonMap(rs, properties, model);
            String subjectURI = RiverUtils.replaceResourceURI(rs.toString(), resourceURIFragments);

            //If updateDocuments is set to true, then prepare to update this document
            if (updateDocuments) {
                prepareUpdateDocument(bulkRequest, convertSingleValueListToString(jsonMap), subjectURI);
            } else {
                //Otherwise, prepare to index this document
                prepareIndexDocument(bulkRequest, convertSingleValueListToString(jsonMap), subjectURI);
            }

            bulkLength++;
            // We want to execute the bulk for every numberOfBulkActions requests
            if (bulkLength % numberOfBulkActions == 0) {
                BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                bulkRequest = client.prepareBulk();// After executing, clear the BulkRequestBuilder.
                if (bulkResponse.hasFailures()) {
                    // Handle failure by iterating through each bulk response item
                    processBulkResponseFailure(bulkResponse);
                }
            }
        }
        // Execute remaining requests
        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse response = bulkRequest.execute().actionGet();
            if (response.hasFailures()) {
                processBulkResponseFailure(response);
            }
        }

        long finishTime = System.currentTimeMillis();
        //Show time taken to perform the action
        String actionPerformed = updateDocuments ? "updated" : "indexed";
        logger.info("\n-------------------------------------------"
                + "\n\tTotal documents " + actionPerformed + ": " + bulkLength
                + "\n\tTriples: " + model.size()
                + "\n\tRiver: " + riverName
                + "\n\tIndex: " + indexName
                + "\n\tType: " + typeName
                + "\n\tTime to index: " + getTimeString(finishTime - startTime)
                + "\n\tTotal time (query + index): " + getTimeString(finishTime - getTimeStarted())
                + "\n-------------------------------------------");
    }

    /**
     * This method processes failures by iterating through each bulk
     * response item
     *
     * @param response, a BulkResponse
     */
    private void processBulkResponseFailure(BulkResponse response) {
        logger.warn("There was failures when executing bulk : " + response.buildFailureMessage());

        if (!logger.isDebugEnabled()) {
            return;
        }
        for (BulkItemResponse item : response.getItems()) {
            if (item.isFailed()) {
                logger.debug("Error {} occurred on index {}, type {}, id {} for {} operation ",
                        item.getFailureMessage(),
                        item.getIndex(),
                        item.getType(),
                        item.getId(),
                        item.getOpType());
            }

        }
    }

    /**
     * Prepare update of a document in ElasticSearch. Given a document ID,
     * document will be merged to the existing document with this ID, if
     * original document does not exist, no update will be performed and the
     * DocumentMissingException will be thrown.<b>
     * <p>
     * This is useful if someone wants to update a partial document in
     * ElasticSearch without full re-indexing. Hemed, 09-03-2015
     */
    private void prepareUpdateDocument(BulkRequestBuilder bulkRequest, Map<String, Object> source, String documentId) {
        bulkRequest.add(client
                .prepareUpdate(indexName, typeName, documentId)
                //Merge this document to the existing one of the same Id.
                .setDoc(source));
    }

    /**
     * Prepare document to be bulk indexed in Elasticsearch
     */
    private void prepareIndexDocument(BulkRequestBuilder bulkRequest, Map<String, Object> source, String documentId) {
        bulkRequest.add(client
                .prepareIndex(indexName, typeName, documentId)
                .setSource(source));
    }

    /**
     * Converts a map of results to a String JSON representation for it
     *
     * @param map a map that matches properties with an ArrayList of values
     * @return the JSON representation for the map, as a String
     */
    private String mapToString(Map<String, ArrayList<String>> map) {
        StringBuilder result = new StringBuilder("{");
        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            ArrayList<String> value = entry.getValue();
            if (value.size() == 1) {
                result.append(String.format("\"%s\" : %s,\n",
                        entry.getKey(), value.get(0)));
            } else {
                result.append(String.format("\"%s\" : %s,\n",
                        entry.getKey(), value));
            }
        }

        result.setCharAt(result.length() - 2, '}');
        return result.toString();
    }

    /**
     * If input map contains a list of only one element, then convert the list to string.
     *
     * @param map input map
     * @return a map where a value of type ArrayList that has only one element is converted to a string.
     */
    private Map<String, Object> convertSingleValueListToString(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof List) {
                List value = (List) entry.getValue();
                if (value.size() == 1) {
                    map.put(entry.getKey(), value.get(0));
                }
            }
        }
        return map;
    }

    /**
     * Builds a String result for Elastic Search from an RDFNode
     *
     * @param resource An RDF resource
     * @param node     An RDFNode representing the value of a property for a
     *                 given resource
     * @return If the RDFNode has a Literal value, among Boolean, Byte,
     * Double, Float, Integer Long, Short, this value is returned, converted
     * to String
     * <p>
     * If the RDFNode has a String Literal value, this value will be
     * returned, surrounded by double quotes </p>
     * <p>
     * If the RDFNode has a Resource value (URI) and toDescribeURIs is set
     * to true, the value of @getLabelForUri for the resource is returned,
     * surrounded by double quotes.</p>
     * Otherwise, the URI will be returned
     */
    private String getStringForResult(Resource resource, RDFNode node) {
        String result = "";
        boolean quote = false;
        if (node.isLiteral()) {
            try {
                Object literalValue = node.asLiteral().getValue();
                Class<?> literalJavaClass = node.asLiteral()
                        .getDatatype()
                        .getJavaClass();

                if (literalJavaClass.equals(Boolean.class)
                        || Number.class.isAssignableFrom(literalJavaClass)) {

                    result += literalValue;
                } else {
                    result = RiverUtils.parseForJson(node.asLiteral().getLexicalForm());
                    quote = true;
                }
            } catch (java.lang.NullPointerException npe) {
                result = RiverUtils.parseForJson(node.asLiteral().getLexicalForm());
                quote = true;
            } catch (Exception e) {
                logger.warn("Exception when retrieving literal value from a property [{}]" +
                                " of resource [{}]  with details [{}]. The property will not be indexed",
                        node.toString(), resource.toString(), e.getLocalizedMessage());
            }

        } else if (node.isResource()) {
            try {
                result = node.asResource().getURI();
                if (toDescribeURIs) {
                    // NOTE: By default, we have excluded possibility of getting labels
                    // from SPARQL endpoint because it was error-prone due to
                    // HTTP Exceptions - too many requests in less than a second
                    // threw BindException - Address already in use.
                    if (tdbDataset != null) {
                        result = getLabelForUriFromTDB(result, tdbDataset);
                    } else {//Fall back
                        if (!rdfEndpoint.isEmpty()) {
                            result = getLabelForUriFromEndpoint(result);
                        }
                    }
                }
                quote = true;
            } catch (Exception ex) {
                logger.warn("Exception when getting resource value from a property [{}] " +
                                "of resource [{}] with details [{}]",
                        node.toString(), resource.toString(), ex.getLocalizedMessage());
            }
        }
        //if(quote) { result = "\"" + result + "\""; }
        return result;
    }

    /**
     * Returns the string value of the first of the properties in the
     * uriDescriptionList for the given resource (as an URI). In case the
     * resource does not have any of the properties mentioned, its URI is
     * returned. The value is obtained by querying the endpoint and the
     * endpoint is queried repeatedly until it gives a response (value or
     * the lack of it)
     * <p>
     * It is highly recommended that the list contains properties like
     * labels or titles, with test values.
     *
     * @param uri - the URI for which a label is required
     * @return a String value, either a label for the parameter or its value
     * if no label is obtained from the endpoint
     */
    private String getLabelForUriFromEndpoint(String uri) {
        String innerQuery = getInnerQueryForLabel(uri);
        try {
            Query query = QueryFactory.create(innerQuery);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(rdfEndpoint, query);
            try {
                ResultSet results = qexec.execSelect();
                String result = getLexicalForm(results);
                if (!result.isEmpty()) {
                    qexec.close();
                    return result;
                }
            } catch (Exception ex) {
                logger.warn("Could not get label for uri [{}] from endpoint [{}] with query [{}] ",
                        uri, rdfEndpoint, innerQuery);
                ex.getLocalizedMessage();
            } finally {
                qexec.close();
            }
        } catch (QueryParseException qpe) {
            logger.error("Exception for query [{}]. "
                    + "Please check your SPARQL query syntax. The label cannot be obtained. " +
                    "Details [{}] ", innerQuery, qpe.getLocalizedMessage());
        }
        return uri;
    }

    /**
     * This method tries to get labels by querying a TDB store instead of
     * SPARQL Endpoint. We have seen that using TDB to get labels from a
     * single JVM is somewhat efficient in the sense that you are capable of
     * doing as many queries as possible within a second without getting
     * HTTP Exceptions or Socket bind exceptions as contrasted to SPARQL
     * endpoints. Hemed Ali, 28-05-2015
     * <p>
     * <p>
     *
     * @param uri - the URI for which a label is required
     * @return a String value, either a label for the parameter or its value
     * if no label is obtained, the URI is returned.
     */
    private String getLabelForUriFromTDB(String uri, Dataset dataset) {
        String innerQuery = getInnerQueryForLabel(uri);
        try {
            Query query = QueryFactory.create(innerQuery);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
                ResultSet results = qexec.execSelect();
                String result = getLexicalForm(results);
                if (!result.isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                logger.warn("Could not get label for uri [{}] from TDB [{}] with query [{}] ",
                        uri, tdbLocation, innerQuery);
                e.getLocalizedMessage();
            }
        } catch (QueryParseException qpe) {
            logger.error("Exception for query [{}]. "
                    + "Please check your SPARQL query syntax. "
                    + "The label cannot be obtained. ", innerQuery);
        }
        return uri;
    }

    /**
     * Gets string representation for the result
     */
    private String getLexicalForm(ResultSet results) {
        if (results.hasNext()) {
            QuerySolution sol = results.nextSolution();
            String result = RiverUtils.parseForJson(sol.getLiteral("label").getLexicalForm());
            if (!result.isEmpty()) {
                return result;
            }
        }
        return "";
    }


    /**
     * A method to build up the query based on the uriDescriptionList. The
     * query will be used to fetch label from the resources. Hemed,
     * 26-05-2015
     *
     * @param uri
     * @return SPARQL query as a string
     * @since 26-05-15 At the end, we need an inner query like the
     * following:- String innerQuery = "SELECT ?label " + "WHERE { GRAPH ?G
     * { " + "OPTIONAL { <" + uri + "> " +
     * "<http://www.w3.org/2000/01/rdf-schema#label> ?rdfLabel } " +
     * "OPTIONAL { <" + uri + "> " + "<http://xmlns.com/foaf/0.1/name>
     * ?foafName } " + "OPTIONAL { <" + uri + "> " +
     * "<http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel } " +
     * "BIND(COALESCE(?foafName,?rdfLabel,?prefLabel) AS ?label) " + "}} "
     */
    private String getInnerQueryForLabel(String uri) {

        String options = "";
        String bind = "";
        int count = 0;
        String labelCoalesce = "";

        //This is too specific to the University of Bergen Library´s Ontology.
        //In the future, you might want to let the default language be automatically picked up.
        String filter = "FILTER (langMatches(lang(?label), \"\") || langMatches(lang(?label), \"" + language + "\")) ";

        //Iterate over the list and build up the options.
        for (String property : uriDescriptionList) {
            String label = "?label" + count++;
            options += "OPTIONAL { <" + uri + "> " + "<" + property + "> " + label + " } ";
            labelCoalesce += label + ",";
        }
        //Build up coalesce string, this function checks the label for the first occurrence in sequential order,
        //and if the label was not found, it checks for the next one.
        bind += "BIND(COALESCE(" + labelCoalesce.substring(0, labelCoalesce.length() - 1) + ") AS ?label) ";

        //Build up the final query to fetch the corresponding label.
        return "SELECT ?label "
                + "WHERE { GRAPH ?G { " + options + bind + "} " + filter + "} " + "LIMIT 1";
    }

    private enum QueryType {
        SELECT,
        CONSTRUCT,
        DESCRIBE
    }
}
