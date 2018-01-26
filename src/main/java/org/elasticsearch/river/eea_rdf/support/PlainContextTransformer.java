package org.elasticsearch.river.eea_rdf.support;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.util.HashMap;
import java.util.Map;

public class PlainContextTransformer extends ContextTransformer {

    private static final String CONTEXT_KEY = "@context";
    private static final String CONTEXT_ID = "@id";
    private final ESLogger logger = Loggers.getLogger(getClass().getName());
    private Map<String, String> contextProperties = new HashMap<>();
    private JsonElement context;


    PlainContextTransformer(String context) {
        this.context = extractContext(context);
    }


    public JsonElement getContext() {
        return context;
    }

    /**
     * Convert context to Map of String values
     */
    @Override
    public Map<String, String> transform() {
        for (Map.Entry<String, JsonElement> entry : getContext().getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                String value = entry.getValue().getAsJsonObject().get(CONTEXT_ID).getAsString();
                contextProperties.put(value, entry.getKey());
            } else if (entry.getValue().isJsonArray()) {
                logger.warn("Expected URI but found JSON array in the context for property [{}]." +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonNull()) {
                logger.warn("Expected URI but found JSON Null in the context for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonPrimitive()) {
                logger.warn("Expected URI but found primitive data type in the context for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else {
                contextProperties.put(entry.getValue().toString(), entry.getKey());
            }
        }
        return contextProperties;
    }


        /**
         * Extract context key as a JsonElement
         */
        private JsonElement extractContext(String context){
            JsonElement con = new JsonParser().parse(context);
            if (con.isJsonObject() && con.getAsJsonObject().getAsJsonObject(CONTEXT_KEY) != null) {
                return con.getAsJsonObject().getAsJsonObject(CONTEXT_KEY);
            }
            throw new IllegalArgumentException("JSON context must have @context key");
        }


        public static void main(String [] args) {
            String s = "{\n" +
                    "@context: {\n" +
                    "Symbolic: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/Symbolic\"\n" +
                    "},\n" +
                    "spatial: {\n" +
                    "@id: \"http://purl.org/dc/terms/spatial\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P109_has_current_or_former_curator: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P109_has_current_or_former_curator\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "birthName: {\n" +
                    "@id: \"http://dbpedia.org/ontology/birthName\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "dateAccepted: {\n" +
                    "@id: \"http://purl.org/dc/terms/dateAccepted\"\n" +
                    "},\n" +
                    "physicalDescription: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/physicalDescription\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "hasSMView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasSMView\"\n" +
                    "},\n" +
                    "abstract: {\n" +
                    "@id: \"http://purl.org/dc/terms/abstract\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "clause: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/clause\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#boolean\"\n" +
                    "},\n" +
                    "P11i_participated_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P11i_participated_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "backgroundColor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/backgroundColor\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "utm33: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/utm33\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "E22_Man-Made_Object: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E22_Man-Made_Object\"\n" +
                    "},\n" +
                    "E63_Beginning_of_Existence: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E63_Beginning_of_Existence\"\n" +
                    "},\n" +
                    "E56_Language: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E56_Language\"\n" +
                    "},\n" +
                    "isFormerOwnerOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isFormerOwnerOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P141i_was_assigned_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P141i_was_assigned_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "reboundAfter: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/reboundAfter\"\n" +
                    "},\n" +
                    "scopeNote: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#scopeNote\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "techniqueOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/techniqueOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P151_was_formed_from: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P151_was_formed_from\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P111_added: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P111_added\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasURI: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasURI\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#anyURI\"\n" +
                    "},\n" +
                    "P108i_was_produced_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P108i_was_produced_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P100i_died_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P100i_died_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Attachment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Attachment\"\n" +
                    "},\n" +
                    "accountCategory: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/accountCategory\"\n" +
                    "},\n" +
                    "AppelationType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/AppelationType\"\n" +
                    "},\n" +
                    "parcelNumber: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/parcelNumber\"\n" +
                    "},\n" +
                    "Magazine: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Magazine\"\n" +
                    "},\n" +
                    "publisherOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/publisherOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P126i_was_employed_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P126i_was_employed_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Copy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Copy\"\n" +
                    "},\n" +
                    "owner: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/owner\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isPageTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isPageTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P29i_received_custody_through: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P29i_received_custody_through\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P32i_was_technique_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P32i_was_technique_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Clause: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Clause\"\n" +
                    "},\n" +
                    "P89i_contains: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P89i_contains\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "mergeWith: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/mergeWith\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P11_had_participant: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P11_had_participant\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Telegram: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Telegram\"\n" +
                    "},\n" +
                    "versionInfo: {\n" +
                    "@id: \"http://www.w3.org/2002/07/owl#versionInfo\"\n" +
                    "},\n" +
                    "created: {\n" +
                    "@id: \"http://purl.org/dc/terms/created\"\n" +
                    "},\n" +
                    "EditedBook: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/EditedBook\"\n" +
                    "},\n" +
                    "P112i_was_diminished_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P112i_was_diminished_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Painting: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Painting\"\n" +
                    "},\n" +
                    "pageStart: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/pageStart\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "begin: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/begin\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "timeSpan: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/timeSpan\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E9_Move: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E9_Move\"\n" +
                    "},\n" +
                    "ShareCertificate: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ShareCertificate\"\n" +
                    "},\n" +
                    "isNextInSequence: {\n" +
                    "@id: \"http://www.europeana.eu/schemas/edm/isNextInSequence\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "profession: {\n" +
                    "@id: \"http://dbpedia.org/ontology/profession\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "CopyBook: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/CopyBook\"\n" +
                    "},\n" +
                    "P46_is_composed_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P46_is_composed_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "width: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/width\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "P27_moved_from: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P27_moved_from\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "externalResource: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/externalResource\"\n" +
                    "},\n" +
                    "nextPage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/nextPage\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Publication: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Publication\"\n" +
                    "},\n" +
                    "CircularLetter: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/CircularLetter\"\n" +
                    "},\n" +
                    "isAppelationTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isAppelationTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hiddenLabel: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#hiddenLabel\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "RightsStatement: {\n" +
                    "@id: \"http://purl.org/dc/terms/RightsStatement\"\n" +
                    "},\n" +
                    "E80_Part_Removal: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E80_Part_Removal\"\n" +
                    "},\n" +
                    "license: {\n" +
                    "@id: \"http://purl.org/dc/terms/license\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E10_Transfer_Custody_Of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E10_Transfer_Custody_Of\"\n" +
                    "},\n" +
                    "locationFor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/locationFor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "WebResource: {\n" +
                    "@id: \"http://www.europeana.eu/schemas/edm/WebResource\"\n" +
                    "},\n" +
                    "ProvenanceStatement: {\n" +
                    "@id: \"http://purl.org/dc/terms/ProvenanceStatement\"\n" +
                    "},\n" +
                    "P108_has_produced: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P108_has_produced\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "made: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/made\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E34_Inscription: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E34_Inscription\"\n" +
                    "},\n" +
                    "commissionedBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/commissionedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "priority: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/workflow/priority\"\n" +
                    "},\n" +
                    "isResourceOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isResourceOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "img: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/img\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P52_has_current_owner: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P52_has_current_owner\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Vidisse: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Vidisse\"\n" +
                    "},\n" +
                    "Country: {\n" +
                    "@id: \"http://schema.org/Country\"\n" +
                    "},\n" +
                    "P113_removed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P113_removed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "relatedTo: {\n" +
                    "@id: \"http://schema.org/relatedTo\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isActorTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isActorTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "appliesTo: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/appliesTo\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Vessel: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Vessel\"\n" +
                    "},\n" +
                    "pageEnd: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/pageEnd\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "senderOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/senderOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "body: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/body\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "E39_Actor: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E39_Actor\"\n" +
                    "},\n" +
                    "originalCreatorOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/originalCreatorOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Translation: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Translation\"\n" +
                    "},\n" +
                    "biography: {\n" +
                    "@id: \"http://purl.org/vocab/bio/0.1/biography\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P52i_is_current_owner_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P52i_is_current_owner_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P95i_was_formed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P95i_was_formed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Organization: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/Organization\"\n" +
                    "},\n" +
                    "P92_brought_into_existence: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P92_brought_into_existence\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "issued: {\n" +
                    "@id: \"http://purl.org/dc/terms/issued\"\n" +
                    "},\n" +
                    "P78i_identifies: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P78i_identifies\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P30_transferred_custody_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P30_transferred_custody_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E42_Identifier: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E42_Identifier\"\n" +
                    "},\n" +
                    "Button: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Button\"\n" +
                    "},\n" +
                    "Thing: {\n" +
                    "@id: \"http://www.w3.org/2002/07/owl#Thing\"\n" +
                    "},\n" +
                    "P128_carries: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P128_carries\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "reboundBefore: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/reboundBefore\"\n" +
                    "},\n" +
                    "P16i_was_used_for: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P16i_was_used_for\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "MortageDeed: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/MortageDeed\"\n" +
                    "},\n" +
                    "isGenreFormOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isGenreFormOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P145_separated: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P145_separated\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "PageBlock: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/PageBlock\"\n" +
                    "},\n" +
                    "ingress: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ingress\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "issue: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/issue\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "accountProvenanceOf: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/accountProvenanceOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "cataloguer: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/cataloguer\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasPublication: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPublication\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E14_Condition_Assessment: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E14_Condition_Assessment\"\n" +
                    "},\n" +
                    "references: {\n" +
                    "@id: \"http://purl.org/dc/terms/references\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Room: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Room\"\n" +
                    "},\n" +
                    "E54_Dimension: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E54_Dimension\"\n" +
                    "},\n" +
                    "Proceedings: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Proceedings\"\n" +
                    "},\n" +
                    "Modification: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Modification\"\n" +
                    "},\n" +
                    "P19_was_intended_use_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P19_was_intended_use_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Speech: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Speech\"\n" +
                    "},\n" +
                    "isSubjectOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isSubjectOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "BlockComponent: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/BlockComponent\"\n" +
                    "},\n" +
                    "height: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/height\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "Note: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Note\"\n" +
                    "},\n" +
                    "receivedFrom: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/receivedFrom\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "assessedSection: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/assessedSection\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasBeenMergedWith: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasBeenMergedWith\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "pages: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/pages\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "archivalCategory: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/archivalCategory\"\n" +
                    "},\n" +
                    "place: {\n" +
                    "@id: \"http://purl.org/NET/c4dm/event.owl#place\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isShownAt: {\n" +
                    "@id: \"http://www.europeana.eu/schemas/edm/isShownAt\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P46i_forms_part_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P46i_forms_part_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P10_falls_within: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P10_falls_within\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "SaleDeed: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/SaleDeed\"\n" +
                    "},\n" +
                    "Publisher: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Publisher\"\n" +
                    "},\n" +
                    "E4_Period: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E4_Period\"\n" +
                    "},\n" +
                    "E64_End_of_Existence: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E64_End_of_Existence\"\n" +
                    "},\n" +
                    "Trash: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Trash\"\n" +
                    "},\n" +
                    "Workshop: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Workshop\"\n" +
                    "},\n" +
                    "P35i_was_identified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P35i_was_identified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P35_has_identified: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P35_has_identified\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "viafID: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/viafID\"\n" +
                    "},\n" +
                    "hasVersion: {\n" +
                    "@id: \"http://purl.org/dc/terms/hasVersion\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Grunneiendom: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#Grunneiendom\"\n" +
                    "},\n" +
                    "P140i_was_attributed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P140i_was_attributed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E78_Curated_Holding: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E78_Curated_Holding\"\n" +
                    "},\n" +
                    "Memorandum: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Memorandum\"\n" +
                    "},\n" +
                    "formerOwnerOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/formerOwnerOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "depiction: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/depiction\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Object: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Object\"\n" +
                    "},\n" +
                    "P7_took_place_at: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P7_took_place_at\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isAboutResource: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isAboutResource\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E68_Dissolution: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E68_Dissolution\"\n" +
                    "},\n" +
                    "Map: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Map\"\n" +
                    "},\n" +
                    "P95_has_formed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P95_has_formed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Deed: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Deed\"\n" +
                    "},\n" +
                    "E29_Design_or_Procedure: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E29_Design_or_Procedure\"\n" +
                    "},\n" +
                    "Album: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Album\"\n" +
                    "},\n" +
                    "E31_Document: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E31_Document\"\n" +
                    "},\n" +
                    "P123_resulted_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P123_resulted_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "CartographicMaterial: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/CartographicMaterial\"\n" +
                    "},\n" +
                    "Monument: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Monument\"\n" +
                    "},\n" +
                    "E74_Group: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E74_Group\"\n" +
                    "},\n" +
                    "Audio: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Audio\"\n" +
                    "},\n" +
                    "maker: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/maker\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P25_moved: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P25_moved\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P56i_is_found_on: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P56i_is_found_on\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Conservation_Activity: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/Conservation_Activity\"\n" +
                    "},\n" +
                    "honorificPrefix: {\n" +
                    "@id: \"http://schema.org/honorificPrefix\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "deathPlace: {\n" +
                    "@id: \"http://dbpedia.org/ontology/deathPlace\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "displayLabel: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/displayLabel\"\n" +
                    "},\n" +
                    "parentADM1: {\n" +
                    "@id: \"http://www.geonames.org/ontology#parentADM1\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P4_has_time-span: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P4_has_time-span\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasPageType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPageType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Supplication: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Supplication\"\n" +
                    "},\n" +
                    "long: {\n" +
                    "@id: \"http://www.w3.org/2003/01/geo/wgs84_pos#long\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#double\"\n" +
                    "},\n" +
                    "hasActorType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasActorType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasCondtitionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasCondtitionType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P19i_was_made_for: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P19i_was_made_for\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "deathDate: {\n" +
                    "@id: \"http://dbpedia.org/ontology/deathDate\"\n" +
                    "},\n" +
                    "E81_Transformation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E81_Transformation\"\n" +
                    "},\n" +
                    "altLabel: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#altLabel\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "E7_Activity: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E7_Activity\"\n" +
                    "},\n" +
                    "Parish: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Parish\"\n" +
                    "},\n" +
                    "Feature: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/Feature\"\n" +
                    "},\n" +
                    "label: {\n" +
                    "@id: \"http://www.w3.org/2000/01/rdf-schema#label\"\n" +
                    "},\n" +
                    "P16_used_specific_object: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P16_used_specific_object\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P109i_is_current_or_former_curator_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P109i_is_current_or_former_curator_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "PressClipping: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/PressClipping\"\n" +
                    "},\n" +
                    "CatalogueCard: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/CatalogueCard\"\n" +
                    "},\n" +
                    "E28_Conceptual_Object: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E28_Conceptual_Object\"\n" +
                    "},\n" +
                    "contains: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/contains\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E86_Leaving: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E86_Leaving\"\n" +
                    "},\n" +
                    "assignedContributor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/assignedContributor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Receipt: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Receipt\"\n" +
                    "},\n" +
                    "narrower: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#narrower\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Kommune: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#Kommune\"\n" +
                    "},\n" +
                    "subOrganizationOf: {\n" +
                    "@id: \"http://www.w3.org/ns/org#subOrganizationOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isPartOf: {\n" +
                    "@id: \"http://purl.org/dc/terms/isPartOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P144_joined_with: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P144_joined_with\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P27i_was_origin_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P27i_was_origin_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "editorOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/editorOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "parent: {\n" +
                    "@id: \"http://schema.org/parent\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E3_Condition_State: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E3_Condition_State\"\n" +
                    "},\n" +
                    "DoctoralThesis: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/DoctoralThesis\"\n" +
                    "},\n" +
                    "P78_is_identified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P78_is_identified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P22_transferred_title_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P22_transferred_title_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P10i_contains: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P10i_contains\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "children: {\n" +
                    "@id: \"http://schema.org/children\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "mbox: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/mbox\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "BookOfMinutes: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/BookOfMinutes\"\n" +
                    "},\n" +
                    "hasPart: {\n" +
                    "@id: \"http://purl.org/dc/terms/hasPart\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Concept: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#Concept\"\n" +
                    "},\n" +
                    "recipient: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/recipient\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "focus: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/focus\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasXSView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasXSView\"\n" +
                    "},\n" +
                    "Bill: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Bill\"\n" +
                    "},\n" +
                    "Contract: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Contract\"\n" +
                    "},\n" +
                    "related: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#related\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "familyName: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/familyName\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "measurement: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/measurement\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "sibling: {\n" +
                    "@id: \"http://schema.org/sibling\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "StorageUnit: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/StorageUnit\"\n" +
                    "},\n" +
                    "Location: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Location\"\n" +
                    "},\n" +
                    "add_P2_Type: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ubco/add_P2_Type\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P134_continued: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P134_continued\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "LivingImage: {\n" +
                    "@id: \"http://purl.org/dc/dcmitype/LivingImage\"\n" +
                    "},\n" +
                    "P3_has_note: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P3_has_note\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "containedIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/containedIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "ContributionAssignment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ContributionAssignment\"\n" +
                    "},\n" +
                    "acquired: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/acquired\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasAcquisitionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasAcquisitionType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "invertedName: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/invertedName\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P12_occurred_in_the_presence_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P12_occurred_in_the_presence_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E11_Modification: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E11_Modification\"\n" +
                    "},\n" +
                    "P59i_is_located_on_or_within: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P59i_is_located_on_or_within\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P55i_currently_holds: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P55i_currently_holds\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P98_brought_into_life: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P98_brought_into_life\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Fragment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Fragment\"\n" +
                    "},\n" +
                    "P59_has_section: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P59_has_section\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "sourceFile: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/sourceFile\"\n" +
                    "},\n" +
                    "superEvent: {\n" +
                    "@id: \"http://schema.org/superEvent\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E79_Part_Addition: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E79_Part_Addition\"\n" +
                    "},\n" +
                    "createdYear: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/createdYear\"\n" +
                    "},\n" +
                    "P87i_identifies: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P87i_identifies\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "FinancialStatement: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/FinancialStatement\"\n" +
                    "},\n" +
                    "AcademicArticle: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/AcademicArticle\"\n" +
                    "},\n" +
                    "gender: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/gender\"\n" +
                    "},\n" +
                    "P31i_was_modified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P31i_was_modified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "homepage: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/homepage\"\n" +
                    "},\n" +
                    "accountProvenance: {\n" +
                    "@id: \"http://schema.ub.uib.no/frs/accountProvenance\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "CashBook: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/CashBook\"\n" +
                    "},\n" +
                    "email: {\n" +
                    "@id: \"http://schema.org/email\"\n" +
                    "},\n" +
                    "Event: {\n" +
                    "@id: \"http://purl.org/NET/c4dm/event.owl#Event\"\n" +
                    "},\n" +
                    "is_feature_of: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/is_feature_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Ship: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Ship\"\n" +
                    "},\n" +
                    "isPageComponentOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isPageComponentOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P97i_was_father_for: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P97i_was_father_for\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P30i_custody_transferred_through: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P30i_custody_transferred_through\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P31_has_modified: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P31_has_modified\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P39_measured: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P39_measured\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "exactMatch: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#exactMatch\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isBookSectionTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/isBookSectionTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "parentADM2: {\n" +
                    "@id: \"http://www.geonames.org/ontology#parentADM2\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "parentCountry: {\n" +
                    "@id: \"http://www.geonames.org/ontology#parentCountry\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "sequenceNr: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/sequenceNr\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "Transcription: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Transcription\"\n" +
                    "},\n" +
                    "fylkenr: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#fylkenr\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "EventType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/EventType\"\n" +
                    "},\n" +
                    "wasAssessedIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/wasAssessedIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Person: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/Person\"\n" +
                    "},\n" +
                    "based_near: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/based_near\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasContributionAssignment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasContributionAssignment\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Advertisement: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Advertisement\"\n" +
                    "},\n" +
                    "note: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#note\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "rightsHolder: {\n" +
                    "@id: \"http://purl.org/dc/terms/rightsHolder\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "placeDelivery: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/placeDelivery\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "pseudonym: {\n" +
                    "@id: \"http://dbpedia.org/ontology/pseudonym\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Assessment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Assessment\"\n" +
                    "},\n" +
                    "P55_has_current_location: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P55_has_current_location\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Relation: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Relation\"\n" +
                    "},\n" +
                    "Cantata: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Cantata\"\n" +
                    "},\n" +
                    "BindingAssessment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/BindingAssessment\"\n" +
                    "},\n" +
                    "E35_Title: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E35_Title\"\n" +
                    "},\n" +
                    "E67_Birth: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E67_Birth\"\n" +
                    "},\n" +
                    "P41i_was_classified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P41i_was_classified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "MusicalWork: {\n" +
                    "@id: \"http://dbpedia.org/ontology/MusicalWork\"\n" +
                    "},\n" +
                    "MovingImage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/MovingImage\"\n" +
                    "},\n" +
                    "E58_Measurement_Unit: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E58_Measurement_Unit\"\n" +
                    "},\n" +
                    "producedIn: {\n" +
                    "@id: \"http://purl.org/NET/c4dm/event.owl#producedIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P126_employed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P126_employed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P17i_motivated: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P17i_motivated\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "notation: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#notation\"\n" +
                    "},\n" +
                    "P91i_is_unit_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P91i_is_unit_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasPage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPage\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Report: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Report\"\n" +
                    "},\n" +
                    "inheritFrom: {\n" +
                    "@id: \"http://www.w3.org/ns/odrl/2/inheritFrom\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "surrenderedObjectThrough: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/surrenderedObjectThrough\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "MapSet: {\n" +
                    "@id: \"http://data.ub.uib.no/MapSet\"\n" +
                    "},\n" +
                    "E44_Place_Appellation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E44_Place_Appellation\"\n" +
                    "},\n" +
                    "E73_Information_Object: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E73_Information_Object\"\n" +
                    "},\n" +
                    "P17_was_motivated_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P17_was_motivated_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P40i_was_observed_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P40i_was_observed_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasRepresentation: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasRepresentation\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "product: {\n" +
                    "@id: \"http://purl.org/NET/c4dm/event.owl#product\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Book: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Book\"\n" +
                    "},\n" +
                    "ownedBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ownedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Agent: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/Agent\"\n" +
                    "},\n" +
                    "Ticket: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Ticket\"\n" +
                    "},\n" +
                    "P26_moved_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P26_moved_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Box: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Box\"\n" +
                    "},\n" +
                    "GenreForm: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/GenreForm\"\n" +
                    "},\n" +
                    "creator: {\n" +
                    "@id: \"http://purl.org/dc/elements/1.1/creator\"\n" +
                    "},\n" +
                    "madeAfter: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/madeAfter\"\n" +
                    "},\n" +
                    "P147i_was_curated_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P147i_was_curated_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P124_transformed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P124_transformed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "name: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/name\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "postalCode: {\n" +
                    "@id: \"http://schema.org/postalCode\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "Technique: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Technique\"\n" +
                    "},\n" +
                    "E5_Event: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E5_Event\"\n" +
                    "},\n" +
                    "E21_Person: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E21_Person\"\n" +
                    "},\n" +
                    "acquiredIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/acquiredIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Role: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Role\"\n" +
                    "},\n" +
                    "hasDimensionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasDimensionType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Picture: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Picture\"\n" +
                    "},\n" +
                    "P100_was_death_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P100_was_death_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Drawing: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Drawing\"\n" +
                    "},\n" +
                    "T-Shirt: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/T-Shirt\"\n" +
                    "},\n" +
                    "P110_augmented: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P110_augmented\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P56_bears_feature: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P56_bears_feature\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Building: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Building\"\n" +
                    "},\n" +
                    "uuid: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/uuid\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Article: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Article\"\n" +
                    "},\n" +
                    "Circulaire: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Circulaire\"\n" +
                    "},\n" +
                    "published: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/published\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "reciever: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/reciever\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasMember: {\n" +
                    "@id: \"http://www.w3.org/ns/org#hasMember\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Manuscript: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Manuscript\"\n" +
                    "},\n" +
                    "acquiredFrom: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/acquiredFrom\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "FrontpageThing: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/mfo/FrontpageThing\"\n" +
                    "},\n" +
                    "E52_Time-Span: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E52_Time-Span\"\n" +
                    "},\n" +
                    "showWeb: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/showWeb\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#boolean\"\n" +
                    "},\n" +
                    "location: {\n" +
                    "@id: \"http://www.w3.org/2003/01/geo/wgs84_pos#location\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P146_separated_from: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P146_separated_from\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P138i_has_representation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P138i_has_representation\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Postcard: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Postcard\"\n" +
                    "},\n" +
                    "Issue: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Issue\"\n" +
                    "},\n" +
                    "assessedBindingOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/assessedBindingOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "removeBeforePublication: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/removeBeforePublication\"\n" +
                    "},\n" +
                    "Booklet: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Booklet\"\n" +
                    "},\n" +
                    "typeOfDamage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/typeOfDamage\"\n" +
                    "},\n" +
                    "originalCreator: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/originalCreator\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "madeBefore: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/madeBefore\"\n" +
                    "},\n" +
                    "MasterThesis: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/MasterThesis\"\n" +
                    "},\n" +
                    "Form: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Form\"\n" +
                    "},\n" +
                    "rights: {\n" +
                    "@id: \"http://purl.org/dc/terms/rights\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "ActorType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ActorType\"\n" +
                    "},\n" +
                    "P43_has_dimension: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P43_has_dimension\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "birthYear: {\n" +
                    "@id: \"http://dbpedia.org/ontology/birthYear\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#gYear\"\n" +
                    "},\n" +
                    "technique: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/technique\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P104_is_subject_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P104_is_subject_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Family: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Family\"\n" +
                    "},\n" +
                    "PrayerBook: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/PrayerBook\"\n" +
                    "},\n" +
                    "PhotographicTechnique: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/PhotographicTechnique\"\n" +
                    "},\n" +
                    "Work: {\n" +
                    "@id: \"http://vocab.org/frbr/core#Work\"\n" +
                    "},\n" +
                    "E66_Formation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E66_Formation\"\n" +
                    "},\n" +
                    "hasCatalogued: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasCatalogued\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "deathYear: {\n" +
                    "@id: \"http://dbpedia.org/ontology/deathYear\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#gYear\"\n" +
                    "},\n" +
                    "P131i_identifies: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P131i_identifies\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P12i_was_present_at: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P12i_was_present_at\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "firstName: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/firstName\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P75i_is_possessed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P75i_is_possessed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "numberOfParts: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/numberOfParts\"\n" +
                    "},\n" +
                    "deprecatedOn: {\n" +
                    "@id: \"http://creativecommons.org/ns#deprecatedOn\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "hasPageBlock: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPageBlock\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P22i_acquired_title_through: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P22i_acquired_title_through\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "DigitalResourceType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/DigitalResourceType\"\n" +
                    "},\n" +
                    "clauseFor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/clauseFor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "rodeNr: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/rodeNr\"\n" +
                    "},\n" +
                    "identifier: {\n" +
                    "@id: \"http://purl.org/dc/terms/identifier\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Protocol: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Protocol\"\n" +
                    "},\n" +
                    "Poster: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Poster\"\n" +
                    "},\n" +
                    "isEventTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isEventTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "wasRecipientOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasRecipientOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P93i_was_taken_out_of_existence_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P93i_was_taken_out_of_existence_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Company: {\n" +
                    "@id: \"http://dbpedia.org/ontology/Company\"\n" +
                    "},\n" +
                    "gnr: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#gnr\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "addAssertedClass: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/addAssertedClass\"\n" +
                    "},\n" +
                    "E26_Physical_Feature: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E26_Physical_Feature\"\n" +
                    "},\n" +
                    "P32_used_general_technique: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P32_used_general_technique\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "deprecated: {\n" +
                    "@id: \"http://www.w3.org/2002/07/owl#deprecated\"\n" +
                    "},\n" +
                    "P65i_is_shown_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P65i_is_shown_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Photograph: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Photograph\"\n" +
                    "},\n" +
                    "P104i_applies_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P104i_applies_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "DimensionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/DimensionType\"\n" +
                    "},\n" +
                    "BookFeature: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/BookFeature\"\n" +
                    "},\n" +
                    "subject: {\n" +
                    "@id: \"http://purl.org/dc/terms/subject\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Page: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Page\"\n" +
                    "},\n" +
                    "P42i_was_assigned_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P42i_was_assigned_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "commissioned: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/commissioned\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "depicts: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/depicts\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "moveToProperty: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/moveToProperty\"\n" +
                    "},\n" +
                    "birthPlace: {\n" +
                    "@id: \"http://dbpedia.org/ontology/birthPlace\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P70i_is_documented_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P70i_is_documented_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasMDView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasMDView\"\n" +
                    "},\n" +
                    "spouse: {\n" +
                    "@id: \"http://schema.org/spouse\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P144i_gained_member_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P144i_gained_member_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "wasSentTo: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasSentTo\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isPageBlockOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isPageBlockOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasTimeline: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasTimeline\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Aggregation: {\n" +
                    "@id: \"http://www.openarchives.org/ore/terms/Aggregation\"\n" +
                    "},\n" +
                    "ConditionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ConditionType\"\n" +
                    "},\n" +
                    "hasFormerOwner: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasFormerOwner\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "classHierarchyURI: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/classHierarchyURI\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#anyURI\"\n" +
                    "},\n" +
                    "dateOfAcquisition: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/dateOfAcquisition\"\n" +
                    "},\n" +
                    "P93_took_out_of_existence: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P93_took_out_of_existence\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P68_foresees_use_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P68_foresees_use_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Point: {\n" +
                    "@id: \"http://www.w3.org/2003/01/geo/wgs84_pos#Point\"\n" +
                    "},\n" +
                    "E82_Actor_Appellation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E82_Actor_Appellation\"\n" +
                    "},\n" +
                    "P65_shows_visual_item: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P65_shows_visual_item\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "definition: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#definition\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P2_has_type: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P2_has_type\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "knows: {\n" +
                    "@id: \"http://schema.org/knows\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "editorialNote: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#editorialNote\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "isContributionAssignmentOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isContributionAssignmentOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P2i_is_type_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P2i_is_type_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E53_Place: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E53_Place\"\n" +
                    "},\n" +
                    "honorificSuffix: {\n" +
                    "@id: \"http://schema.org/honorificSuffix\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P91_has_unit: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P91_has_unit\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isLocationFor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isLocationFor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P43i_is_dimension_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P43i_is_dimension_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P25i_moved_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P25i_moved_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Cataloguer: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Cataloguer\"\n" +
                    "},\n" +
                    "isCondtitionTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isCondtitionTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Minutes: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Minutes\"\n" +
                    "},\n" +
                    "postamble: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/postamble\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "hasTHView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasTHView\"\n" +
                    "},\n" +
                    "stores: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/stores\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "ConceptScheme: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#ConceptScheme\"\n" +
                    "},\n" +
                    "P141_assigned: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P141_assigned\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Flyer: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Flyer\"\n" +
                    "},\n" +
                    "ProxyCollection: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ProxyCollection\"\n" +
                    "},\n" +
                    "isPreferredIdentifierOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isPreferredIdentifierOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E45_Address: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E45_Address\"\n" +
                    "},\n" +
                    "ShipsLog: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ShipsLog\"\n" +
                    "},\n" +
                    "reference: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/reference\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P131_is_identified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P131_is_identified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "GraphicArt: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/GraphicArt\"\n" +
                    "},\n" +
                    "featured: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/featured\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#boolean\"\n" +
                    "},\n" +
                    "gabNr: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/gabNr\"\n" +
                    "},\n" +
                    "E49_Time_Appellation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E49_Time_Appellation\"\n" +
                    "},\n" +
                    "streetAddress: {\n" +
                    "@id: \"http://schema.org/streetAddress\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "hasBookSectionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/hasBookSectionType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "title: {\n" +
                    "@id: \"http://purl.org/dc/terms/title\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "AcquisitionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/AcquisitionType\"\n" +
                    "},\n" +
                    "Bestallingsbrev: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Bestallingsbrev\"\n" +
                    "},\n" +
                    "issn: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/issn\"\n" +
                    "},\n" +
                    "subEvent: {\n" +
                    "@id: \"http://purl.org/NET/c4dm/event.owl#subEvent\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "dateSubmitted: {\n" +
                    "@id: \"http://purl.org/dc/terms/dateSubmitted\"\n" +
                    "},\n" +
                    "polygon: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/polygon\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Passport: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Passport\"\n" +
                    "},\n" +
                    "transcriptionOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/transcriptionOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P70_documents: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P70_documents\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P102_has_title: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P102_has_title\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P89_falls_within: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P89_falls_within\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "placeOfPublication: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/placeOfPublication\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "baseFor: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/baseFor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "birthDate: {\n" +
                    "@id: \"http://dbpedia.org/ontology/birthDate\"\n" +
                    "},\n" +
                    "P33i_was_used_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P33i_was_used_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "publisher: {\n" +
                    "@id: \"http://purl.org/dc/terms/publisher\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E85_Joining: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E85_Joining\"\n" +
                    "},\n" +
                    "E37_Mark: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E37_Mark\"\n" +
                    "},\n" +
                    "Correspondence: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Correspondence\"\n" +
                    "},\n" +
                    "DegreeCertificate: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/DegreeCertificate\"\n" +
                    "},\n" +
                    "kulturnavId: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/kulturnavId\"\n" +
                    "},\n" +
                    "lat: {\n" +
                    "@id: \"http://www.w3.org/2003/01/geo/wgs84_pos#lat\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#double\"\n" +
                    "},\n" +
                    "isPublishedIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isPublishedIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "originPlace: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/originPlace\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P68i_use_foreseen_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P68i_use_foreseen_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "wasAssignedContributionBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasAssignedContributionBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "previousPage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/previousPage\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "assignedAppelationTo: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/assignedAppelationTo\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Exhibition: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Exhibition\"\n" +
                    "},\n" +
                    "hasThumbnail: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasThumbnail\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "depth: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/depth\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "hasBindingAssessment: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/hasBindingAssessment\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P45_consists_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P45_consists_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "catalogueStatus: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/catalogueStatus\"\n" +
                    "},\n" +
                    "inRoleAs: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/inRoleAs\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P86_falls_within: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P86_falls_within\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P102i_is_title_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P102i_is_title_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E16_Measurement: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E16_Measurement\"\n" +
                    "},\n" +
                    "P151i_participated_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P151i_participated_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "provenance: {\n" +
                    "@id: \"http://purl.org/dc/terms/provenance\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "from: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/from\"\n" +
                    "},\n" +
                    "Thesis: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Thesis\"\n" +
                    "},\n" +
                    "publishedYear: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/publishedYear\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#gYear\"\n" +
                    "},\n" +
                    "accessProvidedBy: {\n" +
                    "@id: \"http://data.archiveshub.ac.uk/def/accessProvidedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "mainImage: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/mainImage\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#boolean\"\n" +
                    "},\n" +
                    "volume: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/volume\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "E17_Type_Assignment: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E17_Type_Assignment\"\n" +
                    "},\n" +
                    "hasGenreForm: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasGenreForm\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Fylke: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#Fylke\"\n" +
                    "},\n" +
                    "P96_by_mother: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P96_by_mother\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "topConceptOf: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#topConceptOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isReferencedBy: {\n" +
                    "@id: \"http://purl.org/dc/terms/isReferencedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isRightsHolderOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isRightsHolderOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E41_Appellation: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E41_Appellation\"\n" +
                    "},\n" +
                    "P145i_left_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P145i_left_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "certainty: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/certainty\"\n" +
                    "},\n" +
                    "P42_assigned: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P42_assigned\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "logo: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/logo\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "DigitalResource: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/DigitalResource\"\n" +
                    "},\n" +
                    "date: {\n" +
                    "@id: \"http://purl.org/dc/terms/date\"\n" +
                    "},\n" +
                    "seeAlso: {\n" +
                    "@id: \"http://www.w3.org/2000/01/rdf-schema#seeAlso\"\n" +
                    "},\n" +
                    "Series: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Series\"\n" +
                    "},\n" +
                    "WrittenWork: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/WrittenWork\"\n" +
                    "},\n" +
                    "E12_Production: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E12_Production\"\n" +
                    "},\n" +
                    "has_feature: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/has_feature\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Periodical: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Periodical\"\n" +
                    "},\n" +
                    "hasResource: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasResource\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasAppelationType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasAppelationType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Newspaper: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Newspaper\"\n" +
                    "},\n" +
                    "WorkList: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/WorkList\"\n" +
                    "},\n" +
                    "Language: {\n" +
                    "@id: \"http://lexvo.org/ontology#Language\"\n" +
                    "},\n" +
                    "broader: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#broader\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "bibsysID: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bibsysID\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "internalNote: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/internalNote\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Programme: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Programme\"\n" +
                    "},\n" +
                    "P113i_was_removed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P113i_was_removed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "formerOwner: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/formerOwner\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P4i_is_time-span_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P4i_is_time-span_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "RightsAndUse: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/RightsAndUse\"\n" +
                    "},\n" +
                    "P123i_resulted_from: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P123i_resulted_from\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Will: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Will\"\n" +
                    "},\n" +
                    "E69_Death: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E69_Death\"\n" +
                    "},\n" +
                    "P128i_is_carried_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P128i_is_carried_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "LandSurvey: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/LandSurvey\"\n" +
                    "},\n" +
                    "isResourceIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isResourceIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Diary: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Diary\"\n" +
                    "},\n" +
                    "P99i_was_dissolved_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P99i_was_dissolved_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P87_is_identified_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P87_is_identified_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "JourneyLog: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/JourneyLog\"\n" +
                    "},\n" +
                    "hasSubOrganization: {\n" +
                    "@id: \"http://www.w3.org/ns/org#hasSubOrganization\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P14_carried_out_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P14_carried_out_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Acquisition: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Acquisition\"\n" +
                    "},\n" +
                    "mimeType: {\n" +
                    "@id: \"http://www.semanticdesktop.org/ontologies/nie/#mimeType\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "addressLocality: {\n" +
                    "@id: \"http://schema.org/addressLocality\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P29_custody_received_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P29_custody_received_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "SpatialThing: {\n" +
                    "@id: \"http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing\"\n" +
                    "},\n" +
                    "Brochure: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Brochure\"\n" +
                    "},\n" +
                    "extinctionYear: {\n" +
                    "@id: \"http://dbpedia.org/ontology/extinctionYear\"\n" +
                    "},\n" +
                    "Seal: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Seal\"\n" +
                    "},\n" +
                    "E57_Material: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E57_Material\"\n" +
                    "},\n" +
                    "P134i_was_continued_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P134i_was_continued_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "kommunenr: {\n" +
                    "@id: \"http://vocab.lenka.no/geo-deling#kommunenr\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#int\"\n" +
                    "},\n" +
                    "P7i_witnessed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P7i_witnessed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "WindowDisplay: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/WindowDisplay\"\n" +
                    "},\n" +
                    "P92i_was_brought_into_existence_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P92i_was_brought_into_existence_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "memberOf: {\n" +
                    "@id: \"http://www.w3.org/ns/org#memberOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "extinctionDate: {\n" +
                    "@id: \"http://dbpedia.org/ontology/extinctionDate\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "ExLibris: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ExLibris\"\n" +
                    "},\n" +
                    "comment: {\n" +
                    "@id: \"http://www.w3.org/2000/01/rdf-schema#comment\"\n" +
                    "},\n" +
                    "P110i_was_augmented_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P110i_was_augmented_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasClause: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasClause\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Image: {\n" +
                    "@id: \"http://purl.org/dc/terms/Image\"\n" +
                    "},\n" +
                    "P143_joined: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P143_joined\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Document: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Document\"\n" +
                    "},\n" +
                    "License: {\n" +
                    "@id: \"http://creativecommons.org/ns#License\"\n" +
                    "},\n" +
                    "language: {\n" +
                    "@id: \"http://purl.org/dc/terms/language\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "quality: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/quality\"\n" +
                    "},\n" +
                    "reproduced: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/reproduced\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E38_Image: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E38_Image\"\n" +
                    "},\n" +
                    "Lyrics: {\n" +
                    "@id: \"http://purl.org/ontology/mo/Lyrics\"\n" +
                    "},\n" +
                    "page: {\n" +
                    "@id: \"http://xmlns.com/foaf/0.1/page\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P67i_is_referred_to_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P67i_is_referred_to_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasXLView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasXLView\"\n" +
                    "},\n" +
                    "moveToClass: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ubco/moveToClass\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P75_possesses: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P75_possesses\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "reproducedBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/reproducedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "ObjectAspect: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/ObjectAspect\"\n" +
                    "},\n" +
                    "sender: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/sender\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "formationYear: {\n" +
                    "@id: \"http://dbpedia.org/ontology/formationYear\"\n" +
                    "},\n" +
                    "to: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/to\"\n" +
                    "},\n" +
                    "P111i_was_added_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P111i_was_added_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasPreferredIdentifier: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPreferredIdentifier\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "caption: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/caption\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "P143i_was_joined_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P143i_was_joined_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Compilation: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Compilation\"\n" +
                    "},\n" +
                    "storedAt: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/storedAt\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isRepresentationOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isRepresentationOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P138_represents: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P138_represents\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "has_condition_note: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/has_condition_note\"\n" +
                    "},\n" +
                    "hasView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasView\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#anyURI\"\n" +
                    "},\n" +
                    "P147_curated: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P147_curated\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P146i_lost_member_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P146i_lost_member_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "available: {\n" +
                    "@id: \"http://purl.org/dc/terms/available\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "hasLGView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasLGView\"\n" +
                    "},\n" +
                    "previousIdentifier: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/previousIdentifier\"\n" +
                    "},\n" +
                    "P98i_was_born: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P98i_was_born\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P112_diminished: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P112_diminished\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P124i_was_transformed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P124i_was_transformed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P96i_gave_birth: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P96i_gave_birth\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "extent: {\n" +
                    "@id: \"http://purl.org/dc/terms/extent\"\n" +
                    "},\n" +
                    "alternative: {\n" +
                    "@id: \"http://purl.org/dc/terms/alternative\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "Conference: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Conference\"\n" +
                    "},\n" +
                    "E36_Visual_Item: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E36_Visual_Item\"\n" +
                    "},\n" +
                    "BookFeatureType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bbo/BookFeatureType\"\n" +
                    "},\n" +
                    "wasSentBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasSentBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "wasRoleOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasRoleOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Journal: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Journal\"\n" +
                    "},\n" +
                    "P41_classified: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P41_classified\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P40_observed_dimension: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P40_observed_dimension\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasLocation: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasLocation\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P39i_was_measured_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P39i_was_measured_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Reference: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Reference\"\n" +
                    "},\n" +
                    "P33_used_specific_technique: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P33_used_specific_technique\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P86i_contains: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P86i_contains\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "bruksnavn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/bruksnavn\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "acqusitionType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/acqusitionType\"\n" +
                    "},\n" +
                    "E18_Physical_Thing: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E18_Physical_Thing\"\n" +
                    "},\n" +
                    "P99_dissolved: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P99_dissolved\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "parentFeature: {\n" +
                    "@id: \"http://www.geonames.org/ontology#parentFeature\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "E55_Type: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E55_Type\"\n" +
                    "},\n" +
                    "Sigil: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Sigil\"\n" +
                    "},\n" +
                    "end: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/end\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "hasTopConcept: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#hasTopConcept\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "prefLabel: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#prefLabel\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "wasCataloguedBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/wasCataloguedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasPageComponent: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasPageComponent\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "PageType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/PageType\"\n" +
                    "},\n" +
                    "P28i_surrendered_custody_through: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P28i_surrendered_custody_through\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P14i_performed: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P14i_performed\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasEventType: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasEventType\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P45i_is_incorporated_in: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P45i_is_incorporated_in\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "formationDate: {\n" +
                    "@id: \"http://dbpedia.org/ontology/formationDate\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#date\"\n" +
                    "},\n" +
                    "isbn: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/isbn\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "inScheme: {\n" +
                    "@id: \"http://www.w3.org/2004/02/skos/core#inScheme\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "description: {\n" +
                    "@id: \"http://purl.org/dc/terms/description\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "copyrightStatement: {\n" +
                    "@id: \"http://schema.theodi.org/odrs#copyrightStatement\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#anyURI\"\n" +
                    "},\n" +
                    "P140_assigned_attribute_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P140_assigned_attribute_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "isAcquisitionTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isAcquisitionTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasTranscription: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasTranscription\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P28_custody_surrendered_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P28_custody_surrendered_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "pageIn: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/pageIn\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "hasDZIView: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/hasDZIView\"\n" +
                    "},\n" +
                    "format: {\n" +
                    "@id: \"http://purl.org/dc/terms/format\",\n" +
                    "@type: \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "},\n" +
                    "E87_Curation_Activity: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E87_Curation_Activity\"\n" +
                    "},\n" +
                    "relation: {\n" +
                    "@id: \"http://purl.org/dc/elements/1.1/relation\"\n" +
                    "},\n" +
                    "E13_Attribute_Assignment: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E13_Attribute_Assignment\"\n" +
                    "},\n" +
                    "temporal: {\n" +
                    "@id: \"http://purl.org/dc/terms/temporal\"\n" +
                    "},\n" +
                    "P34i_was_assessed_by: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P34i_was_assessed_by\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P26i_was_destination_of: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P26i_was_destination_of\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Charter: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/Charter\"\n" +
                    "},\n" +
                    "physicalCondition: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/physicalCondition\"\n" +
                    "},\n" +
                    "isLanguageOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isLanguageOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Collection: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Collection\"\n" +
                    "},\n" +
                    "P34_concerned: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P34_concerned\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "clauseStatus: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/clauseStatus\"\n" +
                    "},\n" +
                    "isDimensionTypeOf: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/isDimensionTypeOf\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "P67_refers_to: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P67_refers_to\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "BachelorThesis: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/BachelorThesis\"\n" +
                    "},\n" +
                    "P97_from_father: {\n" +
                    "@id: \"http://erlangen-crm.org/current/P97_from_father\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "appelationAssignedBy: {\n" +
                    "@id: \"http://data.ub.uib.no/ontology/appelationAssignedBy\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "editor: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/editor\",\n" +
                    "@type: \"@id\"\n" +
                    "},\n" +
                    "Letter: {\n" +
                    "@id: \"http://purl.org/ontology/bibo/Letter\"\n" +
                    "},\n" +
                    "E30_Right: {\n" +
                    "@id: \"http://erlangen-crm.org/current/E30_Right\"\n" +
                    "}\n" +
                    "}\n" +
                    "} ";

            System.out.println(new PlainContextTransformer(s).transform());
        }


    }
