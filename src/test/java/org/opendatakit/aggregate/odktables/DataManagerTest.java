package org.opendatakit.aggregate.odktables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opendatakit.aggregate.odktables.util.StaticHelpers.list;

import java.util.List;
import java.util.UUID;

import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendatakit.aggregate.odktables.entity.Row;
import org.opendatakit.aggregate.odktables.exception.RowVersionMismatchException;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.persistence.exception.ODKEntityNotFoundException;
import org.opendatakit.common.persistence.exception.ODKEntityPersistException;
import org.opendatakit.common.persistence.exception.ODKTaskLockException;
import org.opendatakit.common.web.CallingContext;
import org.opendatakit.common.web.TestContextFactory;

public class DataManagerTest {

  private CallingContext cc;
  private String tableId;
  private TableManager tm;
  private DataManager dm;
  private List<Row> rows;

  @Before
  public void setUp() throws Exception {
    this.cc = TestContextFactory.getCallingContext();

    this.tableId = T.tableId;
    this.tm = new TableManager(cc);

    tm.createTable(tableId, T.columns);

    this.dm = new DataManager(tableId, cc);
    this.rows = T.rows;
  }

  @After
  public void tearDown() throws Exception {
    try {
      tm.deleteTable(tableId);
    } catch (ODKEntityNotFoundException e) {
      // ignore
    }
  }

  @Test
  public void testGetRowsEmpty() throws ODKDatastoreException {
    List<Row> rows = dm.getRows();
    assertTrue(rows.isEmpty());
  }

  @Test
  public void testInsertRows() throws ODKEntityPersistException, ODKDatastoreException,
      ODKTaskLockException {
    List<Row> actualRows = dm.insertRows(rows);
    assertEquals(2, actualRows.size());
    for (int i = 0; i < rows.size(); i++) {
      val expected = rows.get(i);
      val actual = actualRows.get(i);
      expected.setRowEtag(actual.getRowEtag());
      expected.setGroupOrUserId(actual.getGroupOrUserId());
    }
    assertEquals(rows.get(0), actualRows.get(0));
    assertEquals(rows.get(1), actualRows.get(1));
  }

  @Test(expected = ODKEntityPersistException.class)
  public void testInsertRowsAlreadyExist() throws ODKEntityPersistException, ODKDatastoreException,
      ODKTaskLockException {
    dm.insertRows(rows);
    dm.insertRows(rows);
  }

  @Test
  public void testGetRows() throws ODKDatastoreException, ODKTaskLockException {
    dm.insertRows(rows);
    List<Row> actualRows = dm.getRows();
    for (int i = 0; i < rows.size(); i++) {
      val expected = rows.get(i);
      val actual = actualRows.get(i);
      assertEquals(expected.getRowId(), actual.getRowId());
      assertEquals(expected.getGroupOrUserId(), actual.getGroupOrUserId());
      assertEquals(expected.getValues(), actual.getValues());
    }
  }

  @Test
  public void testGetRow() throws ODKDatastoreException, ODKTaskLockException {
    val expected = Row.forInsert(T.Data.DYLAN.getId(), null, T.Data.DYLAN.getValues());
    dm.insertRow(expected);
    val actual = dm.getRow(T.Data.DYLAN.getId());
    assertEquals(expected, actual);
  }

  @Test
  public void testGetRowDoeNotExist() throws ODKDatastoreException {
    val row = dm.getRow(T.Data.DYLAN.getId());
    assertNull(row);
  }

  @Test
  public void testGetRowNullSafe() throws ODKEntityPersistException, ODKDatastoreException,
      ODKTaskLockException {
    val expected = Row.forInsert(T.Data.DYLAN.getId(), null, T.Data.DYLAN.getValues());
    dm.insertRow(expected);
    val actual = dm.getRowNullSafe(T.Data.DYLAN.getId());
    assertEquals(expected, actual);
  }

  @Test(expected = ODKEntityNotFoundException.class)
  public void testGetRowNullSafeDoesNotExist() throws ODKEntityNotFoundException,
      ODKDatastoreException {
    dm.getRowNullSafe(T.Data.DYLAN.getId());
  }

  @Test
  public void testUpdateRow() throws ODKEntityPersistException, ODKDatastoreException,
      ODKTaskLockException, RowVersionMismatchException {
    rows = dm.insertRows(rows);
    val expected = rows.get(0);
    expected.getValues().put(T.Columns.age, "24");
    Row actual = dm.updateRow(expected);
    assertFalse(expected.getRowEtag().equals(actual.getRowEtag()));
    expected.setRowEtag(actual.getRowEtag());
    assertEquals(expected, actual);
  }

  @Test(expected = RowVersionMismatchException.class)
  public void testUpdateRowVersionMismatch() throws ODKEntityPersistException,
      ODKDatastoreException, ODKTaskLockException, RowVersionMismatchException {
    rows = dm.insertRows(rows);
    val row = rows.get(0);
    row.setRowEtag(UUID.randomUUID().toString());
    dm.updateRow(row);
  }

  @Test(expected = ODKEntityNotFoundException.class)
  public void testUpdateRowDoesNotExist() throws ODKEntityNotFoundException, ODKDatastoreException,
      ODKTaskLockException, RowVersionMismatchException {
    Row row = rows.get(0);
    row.setRowEtag(UUID.randomUUID().toString());
    dm.updateRow(row);
  }

  @Test
  public void testDeleteRows() throws ODKEntityPersistException, ODKDatastoreException,
      ODKTaskLockException {
    dm.insertRows(rows);
    dm.deleteRows(list(T.Data.DYLAN.getId(), T.Data.JOHN.getId()));
    val rows = dm.getRows();
    assertTrue(rows.isEmpty());
  }
}