/**
 * $Id$
 * 
 * $Log$
 * Revision 1.2  2014/07/03 12:53:41  mmaloney
 * debug improvements.
 *
 * 
 * This software was written by Cove Software, LLC ("COVE") under contract
 * to the United States Government. No warranty is provided or implied other 
 * than specific contractual terms between COVE and the U.S. Government.
 *
 * Copyright 2014 U.S. Army Corps of Engineers, Hydrologic Engineering Center.
 * All rights reserved.
 */
package opendcs.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import opendcs.dai.AlgorithmDAI;
import opendcs.dai.CompDependsDAI;
import opendcs.dai.ComputationDAI;
import opendcs.dai.DataTypeDAI;
import opendcs.dai.DeviceStatusDAI;
import opendcs.dai.EnumDAI;
import opendcs.dai.IntervalDAI;
import opendcs.dai.LoadingAppDAI;
import opendcs.dai.PlatformStatusDAI;
import opendcs.dai.PropertiesDAI;
import opendcs.dai.ScheduleEntryDAI;
import opendcs.dai.SiteDAI;
import opendcs.dai.TimeSeriesDAI;
import opendcs.dai.TsGroupDAI;
import opendcs.dai.XmitRecordDAI;
import decodes.db.UnitConverter;
import decodes.sql.DbKey;
import decodes.sql.KeyGenerator;
import decodes.tsdb.BadTimeSeriesException;
import decodes.tsdb.CTimeSeries;
import decodes.tsdb.DbCompParm;
import decodes.tsdb.DbIoException;
import decodes.tsdb.NoSuchObjectException;
import decodes.tsdb.TimeSeriesIdentifier;
import decodes.tsdb.TsGroup;

public interface DatabaseConnectionOwner
{
	/** @return a database connection */
	public Connection getConnection();
	
	/** @return the DECODES database connection */
	public int getDecodesDatabaseVersion();
	
	public void setDecodesDatabaseVersion(int version, String options);
	
	public int getTsdbVersion();
	
	public void setTsdbVersion(int version, String description);
	
	/** True if the underlying DBMS is Oracle */
	public boolean isOracle();
	
	/** @return true if this is HDB */
	public boolean isHdb();
	
	/** @return true if this is CWMS */
	public boolean isCwms();
	
	
	public KeyGenerator getKeyGenerator();
	
	/**
	 * @return a SimpleDateFormat object suitable for use in log messages.
	 */
	public SimpleDateFormat getLogDateFormat();
	
	/**
	 * Return a string representation of the date suitable for inclusion in a SQL
	 * statement.
	 * @param d the date
	 * @return a string representation
	 */
	public String sqlDate(Date d);
	
	/**
	 * Passed a result set and column number, extract the value and return as a
	 * time-zone corrected java.util.Date object.
	 * @param rs the result set
	 * @param column the column number
	 * @return a Java Date object, or null if the column was null
	 */
	public Date getFullDate(ResultSet rs, int column);
	
	/** @return string representation for a boolean value in this db. */
	public String sqlBoolean(boolean v);
	
	/**
	 * Returns the maximum valid run-id for the specified model.
	 * For now, modelID and modelRunID are HDB-specific concepts,
	 * but since they are used throughout the Computation Infrastructure
	 * they are included in the generic interface.
	 * @param modelId the ID of the model
	 * @return the maximum valid run-id for the specified model.
	 */
	public int findMaxModelRunId(int modelId)
		throws DbIoException;
	
	/**
	 * @return model run ID currently in use for writing data. 
	 */
	public int getWriteModelRunId();


	/**
	 * After connecting to the database, we must have an application ID
	 * @return this connection's app id.
	 */
	public DbKey getAppId();

	/**
	 * Some databases (like HDB) store time in DATE objects that other than UTC.
	 * This returns the timezone for interpreting Date/Times in the database.
	 * @return the String time zone ID
	 */
	public String getDatabaseTimezone();
	
	/**
	 * Factory method to make a DAO for enumerations
	 * @return the DAO
	 */
	public EnumDAI makeEnumDAO();
	
	/**
	 * Factory method to make a DAO for properties
	 * @return the DAO
	 */
	public PropertiesDAI makePropertiesDAO();

	/**
	 * Factory method to make a DAO for data types
	 * @return the DAO
	 */
	public DataTypeDAI makeDataTypeDAO();

	/**
	 * Factory method to make a DAO for sites
	 * @return the DAO
	 */
	public SiteDAI makeSiteDAO();
	
	/**
	 * Factory method to make a DAO for xmit records
	 * @return the DAO
	 */
	public XmitRecordDAI makeXmitRecordDao(int maxDays);

	/**
	 * Factory method to make a DAO for loading applications
	 * @return the DAO
	 */
	public LoadingAppDAI makeLoadingAppDAO();
	
	/**
	 * Factory method to make a DAO for algorithms
	 * @return the DAO
	 */
	public AlgorithmDAI makeAlgorithmDAO();

	/**
	 * Factory method to make a DAO for Time Series Groups
	 * @return the DAO
	 */
	public TsGroupDAI makeTsGroupDAO();

	/**
	 * Factory method to make a DAO for Computations
	 * @return the DAO
	 */
	public ComputationDAI makeComputationDAO();
	
	/**
	 * Factory method to make a DAO for Time Series
	 * @return the DAO
	 */
	public TimeSeriesDAI makeTimeSeriesDAO();

	/**
	 * Given a DbCompParm object containing an SDI read from the SQL database,
	 * expand it into all known datatype and site names. Store these back into
	 * the parameter object.
	 * @param siteDatatype the object to expand
	 */
	public void expandSDI(DbCompParm parm)
		throws DbIoException, NoSuchObjectException;

	/**
	 * Called from the methods that read time-series data to handle
	 * cases where a time-series already contains some data which may
	 * be in units different from the storage units. This method builds
	 * a unit converter and returns it if necessary.
	 * It returns null if no conversion is required.
	 * @param cts The CTimeSeries object containing a valid TimeSeriesIdentifier.
	 * @return unit converter if one is required, null if no conversion necessary.
	 */
	public UnitConverter makeUnitConverterForRead(CTimeSeries cts);
	
	/**
	 * Construct a DAO for reading/writing computation dependency information.
	 */
	public CompDependsDAI makeCompDependsDAO();
	
	/**
	 * Construct a DAO for reading/writing intervals.
	 * @return the Data Access Object appropriate for this database
	 */
	public IntervalDAI makeIntervalDAO();
	
	/**
	 * Construct a DAO for reading/writing schedule entries and status
	 * @return DAO
	 */
	public ScheduleEntryDAI makeScheduleEntryDAO();
	
	/**
	 * Construct a DAO for reading/writing platform status
	 * @return DAO
	 */
	public PlatformStatusDAI makePlatformStatusDAO();
	
	/**
	 * Expanding a time series group is customized for each database.
	 * @param tsGroup
	 * @return
	 * @throws DbIoException
	 */
	public ArrayList<TimeSeriesIdentifier> expandTsGroup(TsGroup tsGroup)
		throws DbIoException;
	
	/**
	 * Take a time-series identifier and transform it by the values
	 * specified in the computation parameter. The param could change
	 * any or all of the parts of the ID.
	 * This is implemented by the database-specific subclass.
	 * Contract:
	 * Do not modify the input tsid object in any way.
	 * If modifications are made a new TimeSeriesIdentifier is returned
	 * If no modifications are made, the method must return then input
	 * object, not a copy of it.
	 * if 'create' is true, and the time-series doesn't exist, create it.
	 * Otherwise, return null if the time series does not exist in the database.
	 * If tsid is null, then create a new TimeSeriesIdentifier from the underlying
	 * database and fill in the parts from the DbCompParm.
	 * @param tsid The TimeSeriesIdentifier to transform
	 * @param parm the computation parameter
	 * @param createTS if true, create the time series if it doesn't exist.
	 * @param fillInParm if true, fill in the parm argument with the resulting
	 * concrete time-series information.
	 * @param timeSeriesDisplayName use this if createTS as the time-series display name.
	 * @return transformed identifier, or null if after transformation, no matching
	 * time series is found in the database
	 * @throws DbIoException if database error
	 * @throws NoSuchObjectException if (createTS) and failed to create TS in database
	 * @throws BadTimeSeriesException on attempt to create new TS with invalid TSID.
	 */
	public abstract TimeSeriesIdentifier transformTsidByCompParm(
		TimeSeriesIdentifier tsid, DbCompParm parm, boolean createTS,
		boolean fillInParm, String timeSeriesDisplayName)
		throws DbIoException, NoSuchObjectException, BadTimeSeriesException;
	
	/**
	 * Construct a DAO for reading writing DeviceStatus structures.
	 */
	public DeviceStatusDAI makeDeviceStatusDAO();
}
