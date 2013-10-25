package de.ubl.marctojson;

import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.MarcFactoryImpl;

public class FatMarcFactory extends MarcFactoryImpl {

    @Override
    public Record newRecord(Leader leader) {
        FatRecord record = new FatRecord();
        record.setLeader(leader);
        return record;
    }
}
