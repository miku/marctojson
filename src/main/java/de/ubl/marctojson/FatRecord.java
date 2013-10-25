package de.ubl.marctojson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.RecordImpl;

public class FatRecord extends RecordImpl {

    public FatRecord() {
        super();
    }

    private static final long serialVersionUID = -8365732068531312656L;

    public String sha1() throws Exception {
        return sha1("UTF-8");
    }

    public String sha1(final String encoding) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MarcWriter writer = new MarcStreamWriter(baos, encoding);
        writer.write(this);
        return DigestUtils.shaHex(baos.toByteArray());
    }

    public String serialize(final String encoding) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MarcWriter writer = new MarcStreamWriter(baos, encoding);
        writer.write(this);
        return baos.toString();
    }

    public String serialize() {
        return serialize("UTF-8");
    }

    public Map<String, Object> toMap() {
        final HashMap<String, Object> contentMap = new HashMap<String, Object>();

        final Leader leader = this.getLeader();
        final HashMap<String, Object> leaderMap = new HashMap<String, Object>();

        leaderMap.put("codingschema", leader.getCharCodingScheme());
        leaderMap.put("entrymap", leader.getEntryMap());
        leaderMap.put("impldef1", leader.getImplDefined1());
        leaderMap.put("impldef2", leader.getImplDefined2());
        leaderMap.put("indicatorcount", leader.getIndicatorCount());
        leaderMap.put("length", leader.getRecordLength());
        leaderMap.put("raw", leader.marshal());
        leaderMap.put("status", leader.getRecordStatus());
        leaderMap.put("subfieldcodelength", leader.getSubfieldCodeLength());
        leaderMap.put("type", leader.getTypeOfRecord());

        contentMap.put("leader", leaderMap);

        final List<ControlField> controlFields = this.getControlFields();
        for (ControlField field : controlFields) {
            // Controlfields are simple
            contentMap.put(field.getTag(), field.getData());
        }

        final List<DataField> dataFields = this.getDataFields();

        for (DataField field : dataFields) {

            if (!contentMap.containsKey(field.getTag())) {
                contentMap.put(field.getTag(), new ArrayList());
            }

            final List<Subfield> subfields = field.getSubfields();
            final HashMap<String, String> subfieldMap = new HashMap<String, String>();
            final List<Map<String, String>> stashed = new ArrayList<Map<String, String>>();

            for (Subfield subfield : subfields) {
            	if (subfieldMap.containsKey(Character.toString(subfield.getCode()))) {
            		// add an extra field
            		Map<String, String> stash = new HashMap<String, String>();
                    stash.put("ind1", Character.toString(field.getIndicator1()));
                    stash.put("ind2", Character.toString(field.getIndicator2()));
            		stash.put(Character.toString(subfield.getCode()), subfield.getData());
            		stashed.add(stash);
            	} else {
            		subfieldMap.put(Character.toString(subfield.getCode()), subfield.getData());
            	}
            }
            subfieldMap.put("ind1", Character.toString(field.getIndicator1()));
            subfieldMap.put("ind2", Character.toString(field.getIndicator2()));
            @SuppressWarnings("unchecked")
			ArrayList<Map<String, String>> dataFieldList = (ArrayList<Map<String, String>>) contentMap.get(field.getTag());
            dataFieldList.add(subfieldMap);
            dataFieldList.addAll(stashed);
            contentMap.put(field.getTag(), dataFieldList);
        }
        return contentMap;
    }

    public Map<String, Object> toMap(Map<String, Object> metadata)
            throws Exception {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("content", this.toMap());
        map.put("content_type", "application/marc");
        map.put("sha1", this.sha1());
        map.put("original", this.serialize());
        if (metadata != null) {
            map.put("meta", metadata);
        }
        return map;
    }

    public String toJson() throws JsonGenerationException,
            JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(this.toMap());
        return json;
    }

    public String toJson(Map<String, Object> metadata) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(this.toMap(metadata));
        return json;
    }

}
