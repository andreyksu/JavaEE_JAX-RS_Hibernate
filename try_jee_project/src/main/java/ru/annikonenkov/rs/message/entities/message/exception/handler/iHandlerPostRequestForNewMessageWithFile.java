package ru.annikonenkov.rs.message.entities.message.exception.handler;

import ru.annikonenkov.rs.message.entities.message.exception.ExceptionParseRequest;

public interface iHandlerPostRequestForNewMessageWithFile {

	public boolean checkIsPresentAllRequiredParameters();

	public Integer getAuthorId() throws ExceptionParseRequest;

	public Integer getReceiverId() throws ExceptionParseRequest;

	public String getMessage() throws ExceptionParseRequest;

	public byte[] getByteArrayOfFile() throws ExceptionParseRequest;

	public String getMediaTypeOfFile();

	public void printCommonInfo();
}
