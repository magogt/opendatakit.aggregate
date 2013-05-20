package org.opendatakit.aggregate.odktables.relation;

import java.util.ArrayList;
import java.util.List;

import org.opendatakit.common.ermodel.simple.Entity;
import org.opendatakit.common.ermodel.simple.Query;
import org.opendatakit.common.ermodel.simple.Relation;
import org.opendatakit.common.persistence.DataField;
import org.opendatakit.common.persistence.DataField.DataType;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.web.CallingContext;

/**
 * The relation in the datastore that maps to the Server KeyValueStore on the 
 * phone.
 * @author sudar.sam@gmail.com
 *
 */
public class DbKeyValueStore {

  // Column names.
  public static final String TABLE_ID = "TABLE_ID";
  public static final String PARTITION = "PARTITION";
  public static final String ASPECT = "ASPECT";
  public static final String KEY = "KEY";
  public static final String TYPE = "TYPE";
  public static final String VALUE = "VALUE";
  
  // The name of the table/relation in the datastore.
  private static final String RELATION_NAME = "KEY_VALUE_STORE";
  
  private static final List<DataField> dataFields;
  static {
    dataFields = new ArrayList<DataField>();
    dataFields.add(new DataField(TABLE_ID, DataType.STRING, false));
    dataFields.add(new DataField(PARTITION, DataType.STRING, false));
    dataFields.add(new DataField(ASPECT, DataType.STRING, false));
    dataFields.add(new DataField(KEY, DataType.STRING, false));
    dataFields.add(new DataField(TYPE, DataType.STRING, false));
    dataFields.add(new DataField(VALUE, DataType.STRING, true));
  }
  
  public static Relation getRelation(CallingContext cc) 
      throws ODKDatastoreException {
    Relation relation = 
        new Relation(RUtil.NAMESPACE, RELATION_NAME, dataFields, cc);
    return relation;
  }
  
  /**
   * Delete all the key value store entities for the given table.
   * <p>
   * NB: No logging is performed! Currently no notion of transactions, so if 
   * this method is called and a pursuant add of new entities fails, there will
   * be no recourse to restore the state.
   * @param tableId
   * @param cc
   * @throws ODKDatastoreException
   */
  public static void clearAllEntries(String tableId, CallingContext cc)
      throws ODKDatastoreException {
    List<Entity> kvsEntities = getKVSEntries(tableId, cc);
    // Does this really delete all the entries? Must be a lot going on in the
    // backend to obtain the Relation and everything statically.
    Relation.deleteEntities(kvsEntities, cc);
  }
  
  /**
   * Get a List of Entity objects representing all the entries in the 
   * key value store for the given table.
   * @param tableId
   * @param cc
   * @return
   * @throws ODKDatastoreException
   */
  public static List<Entity> getKVSEntries(String tableId, CallingContext cc) 
      throws ODKDatastoreException {
    Query query = 
        getRelation(cc).query("DbKeyValueStore.getKVSEntries()", cc);
    query.equal(TABLE_ID, tableId);
    List<Entity> entries = query.execute();
    return entries;
  }
}
