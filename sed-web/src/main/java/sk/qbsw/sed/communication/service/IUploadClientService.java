package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IUploadClientService {

	/**
	 * Uploads clients activities from a file.
	 * 
	 * @param fileRows - array of file rows
	 * @return upload response object
	 * @throws CBussinessDataException
	 */
	CUploadResponseContent uploadActivities(String[] fileRows) throws CBussinessDataException;

	/**
	 * Uploads clients projects from a file.
	 * 
	 * @param fileRows - array of file rows
	 * @return upload response object
	 * @throws CBussinessDataException
	 */
	CUploadResponseContent uploadProjects(String[] fileRows) throws CBussinessDataException;

	/**
	 * Uploads clients employees from a file.
	 * 
	 * @param fileRows - array of file rows
	 * @return upload response object
	 * @throws CBussinessDataException
	 */
	CUploadResponseContent uploadEmployees(String[] fileRows) throws CBussinessDataException;
}
