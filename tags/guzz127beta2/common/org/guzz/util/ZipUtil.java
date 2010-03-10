/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Zip压缩文件的帮助类。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ZipUtil {

	private String inFilePath;

	private String releaseFilePath;

	private String[] FileNameArray; // 存放文件名称的数组

	private ZipEntry entry;

	// 
	private FileInputStream fileDataIn;

	private FileOutputStream fileDataOut;

	private ZipInputStream zipInFile;

	private DataOutputStream writeData;

	private DataInputStream readData;

	//
	private int zipFileCount = 0; // zip文件中的文件总数

	private int zipPathCount = 0; // zip文件中的路径总数

	/**
	 * 初始化函数 初始化zip文件流、输出文件流以及其他变量的初始化
	 */
	public ZipUtil(String inpath, String releasepath) {
		inFilePath = inpath;
		releaseFilePath = releasepath;
	}

	/**
	 * 初始化读取文件流函数 参数：FileInputStream类 返回值：初始化成功返回0，否则返回-1
	 */
	protected long initInStream(ZipInputStream zipFileA) {
		try {
			readData = new DataInputStream(zipFileA);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 测试文件路径 参数：zip文件的路径和要释放的位置 返回值：是两位整数，两位数中的十位代表输入路径和输出路径(1输入、2输出)
	 * 各位数是代表绝对路径还是相对路径(1绝对、0相对) 返回-1表示路径无效
	 * 
	 * protected long checkPath(String inPath,String outPath){ File infile = new
	 * File(inPath); File infile = new File(outPath);
	 *  }
	 */

	/**
	 * 初始化输出文件流 参数：File类 返回值：初始化成功返回0，否则返回-1
	 */
	protected long initOutStream(String outFileA) {
		try {
			fileDataOut = new FileOutputStream(outFileA);
			writeData = new DataOutputStream(fileDataOut);
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 测试文件是否存在方法 参数：File类 返回值：如果文件存在返回文件大小，否则返回-1
	 */
	public long checkFile(File inFileA) {
		if (inFileA.exists()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 判断文件是否可以读取方法 参数：File类 返回值：如果可以读取返回0，否则返回-1
	 */
	public long checkOpen(File inFileA) {
		if (inFileA.canRead()) {
			return inFileA.length();
		} else {
			return -1;
		}
	}

	/**
	 * 获得zip文件中的文件夹和文件总数 参数：File类 返回值：如果正常获得则返回总数，否则返回-1
	 */
	public long getFileFolderCount(String infileA) {
		try {
			int fileCount = 0;
			zipInFile = new ZipInputStream(new FileInputStream(infileA));
			while ((entry = zipInFile.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					zipPathCount++;
				} else {
					zipFileCount++;
				}
				fileCount++;
			}
			return fileCount;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 读取zip文件清单函数 参数：File类 返回值：文件清单数组
	 */
	public String[] getFileList(String infileA) {
		ZipInputStream AzipInFile = null ;
		try {
			AzipInFile = new ZipInputStream(new FileInputStream(
					infileA));
			// 创建数组对象
			FileNameArray = new String[(int) getFileFolderCount(infileA)];

			// 将文件名清单传入数组
			int i = 0;
			while ((entry = AzipInFile.getNextEntry()) != null) {
				FileNameArray[i++] = entry.getName();
			}
			return FileNameArray;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally{
			CloseUtil.close(AzipInFile) ;
		}
	}

	/**
	 * 创建文件函数 参数：File类 返回值:如果创建成功返回0，否则返回-1
	 */
	public long writeFile(String outFileA, byte[] dataByte) {
		try {
			if (initOutStream(outFileA) == 0) {
				writeData.write(dataByte);
				fileDataOut.close();
				return 0;
			} else {
				fileDataOut.close();
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 读取文件内容函数 参数：File类 返回值:如果读取成功则返回读取数据的字节数组，如果失败则返回空值
	 */
	protected byte[] readFile(ZipEntry entryA, ZipInputStream zipFileA) {
		try {
			long entryFilelen;
			if (initInStream(zipFileA) == 0) {
				if ((entryFilelen = entryA.getSize()) >= 0) {
					byte[] entryFileData = new byte[(int) entryFilelen];
					readData.readFully(entryFileData, 0, (int) entryFilelen);
					return entryFileData;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 创建目录函数 参数：要创建目录的路径 返回值：如果创建成功则返回0，否则返回-1
	 */
	public long createFolder(String dir) {
		File file = new File(dir);
		if (file.mkdirs()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 删除文件 参数：要删除的文件 返回值：如果删除成功则返回0，要删除的文件不存在返回-2 如果要删除的是个路径则返回-3，删除失败则返回-1
	 */
	public long deleteFile(String Apath) throws SecurityException {
		File file = new File(Apath.trim());
		// 文件或路径不存在
		if (!file.exists()) {
			return -2;
		}
		// 要删除的是个路径
		if (!file.isFile()) {
			return -3;
		}
		// 删除
		if (file.delete()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 删除目录 参数：要删除的目录 返回值：如果删除成功则返回0，删除失败则返回-1
	 */
	public long deleteFolder(String Apath) {
		File file = new File(Apath);
		// 删除
		if (file.delete()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 判断所要解压的路径是否存在同名文件 参数：解压路径 返回值：如果存在同名文件返回-1，否则返回0
	 */
	public long checkPathExists(String AreleasePath) {
		File file = new File(AreleasePath);
		if (!file.exists()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 删除zip中的文件 参数：文件清单数组，释放路径 返回值：如果删除成功返回0,否则返回-1
	 */
	protected long deleteReleaseZipFile(String[] listFilePath,
			String releasePath) {
		long arrayLen, flagReturn;
		int k = 0;
		String tempPath;
		// 存放zip文件清单的路径
		String[] pathArray = new String[zipPathCount];
		// 删除文件
		arrayLen = listFilePath.length;
		for (int i = 0; i < (int) arrayLen; i++) {
			tempPath = releasePath.replace('\\', '/') + listFilePath[i];
			flagReturn = deleteFile(tempPath);
			if (flagReturn == -2) {
				// 什么都不作
			} else if (flagReturn == -3) {
				pathArray[k++] = tempPath;
			} else if (flagReturn == -1) {
				return -1;
			}
		}
		// 删除路径
		for (k = k - 1; k >= 0; k--) {
			flagReturn = deleteFolder(pathArray[k]);
			if (flagReturn == -1)
				return -1;
		}
		return 0;
	}

	/**
	 * 获得zip文件的最上层的文件夹名称 参数：zip文件路径 返回值：文件夹名称，如果失败则返回null
	 */
	public String getZipRoot(String infileA) {
		String rootName;
		try {
			FileInputStream tempfile = new FileInputStream(infileA);
			ZipInputStream AzipInFile = new ZipInputStream(tempfile);
			ZipEntry Aentry;
			Aentry = AzipInFile.getNextEntry();
			rootName = Aentry.getName();
			tempfile.close();
			AzipInFile.close();
			return rootName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 释放流，释放占用资源
	 */
	protected void closeStream() throws Exception {
		fileDataIn.close();
		fileDataOut.close();
		zipInFile.close();
		writeData.flush();
	}

	/**
	 * 解压函数 对用户的zip文件路径和解压路径进行判断，是否存在和打开 在输入解压路径时如果输入"/"则在和zip文件存放的统计目录下进行解压
	 * 返回值：0表示释放成功 -1 表示您所要解压的文件不存在、 -2表示您所要解压的文件不能被打开、 -3您所要释放的路径不存在、
	 * -4您所创建文件目录失败、 -5写入文件失败、 -6表示所要释放的文件已经存在、 -50表示文件读取异常
	 */
	public long doRelease() throws Exception {
		File inFile = new File(inFilePath);
		File outFile = new File(releaseFilePath);
		String tempFile;
		String zipPath;
		String zipRootPath;
		String tempPathParent; // 存放释放路径
		byte[] zipEntryFileData;

		// 作有效性判断
		if (checkFile(inFile) == -1) {
			return -1;
		}
		if (checkOpen(inFile) == -1) {
			return -2;
		}
		// 不是解压再当前目录下时对路径作有效性检验
		if (!releaseFilePath.equals("/")) {
			// 解压在用户指定目录下
			if (checkFile(outFile) == -1) {
				return -3;
			}
		}
		// 获得标准释放路径
		if (!releaseFilePath.equals("/")) {
			tempPathParent = releaseFilePath.replace('\\', '/') + "/";
		} else {
			tempPathParent = inFile.getParent().replace('\\', '/') + "/";
		}
		// 获得zip文件中的入口清单
		FileNameArray = getFileList(inFilePath);
		// 获得zip文件的最上层目录
//		zipRootPath = getZipRoot(inFilePath);
		//
		fileDataIn = new FileInputStream(inFilePath);
		zipInFile = new ZipInputStream(fileDataIn);
		// 判断是否已经存在要释放的文件夹
//		if (checkPathExists(tempPathParent
//				+ zipRootPath.substring(0, zipRootPath.lastIndexOf("/"))) == -1) {
//			return -6;
//		}
		//
		try {
			// 创建文件夹和文件
			int i = 0;
			while ((entry = zipInFile.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					// 创建目录
					zipPath = tempPathParent + FileNameArray[i];
					zipPath = zipPath.substring(0, zipPath.lastIndexOf("/"));
					if (createFolder(zipPath) == -1) {
						closeStream();
						deleteReleaseZipFile(FileNameArray, tempPathParent);
						return -4;
					}

				} else {
					// 读取文件数据
					zipEntryFileData = readFile(entry, zipInFile);
					// 向文件写数据
					tempFile = tempPathParent + FileNameArray[i];
					// 写入文件
					if (writeFile(tempFile, zipEntryFileData) == -1) {
						closeStream();
						deleteReleaseZipFile(FileNameArray, tempPathParent);
						return -5;
					}
				}
				i++;
			}
			// 释放资源
			closeStream();
			return 0;
		} catch (Exception e) {
			closeStream();
			deleteReleaseZipFile(FileNameArray, tempPathParent);
			e.printStackTrace();
			return -50;
		}
	}

	/**
	 * 把一个Zip文件解压到一个指定的文件夹下。如果文件夹不存在，则自动创建。
	 * 
	 * @param in 要解压的zip文件输入流
	 * @param directory 解压到的目录
	 * @throws Exception 
	 */
	public static long extractZipFileToDirectory(InputStream in, File directory) throws Exception {
		if (!directory.exists()) {
			directory.mkdirs();
		} else if (directory.isFile()) { // 看看是不是文件夹
			throw new IllegalArgumentException("cannot extract to a file["
					+ directory.getAbsolutePath()
					+ "]. file folder is expected.");
		}
		
		File toFile = new File(System.getProperty("tmp"), System.currentTimeMillis() + ".zip") ;
		FileUtil.writeFile(toFile, in) ;
		
		//执行解压缩
		ZipUtil zu = new ZipUtil(toFile.getAbsolutePath(), directory.getAbsolutePath());
		return zu.doRelease();
	}
	
	/**
	 * 把一个Zip文件解压到一个指定的文件夹下。如果文件夹不存在，则自动创建。
	 * 
	 * @param inFile 要解压的zip文件
	 * @param directory 解压到的目录
	 * @throws Exception 
	 */
	public static long extractZipFileToDirectory(File inFile, File directory) throws Exception {
		if (!directory.exists()) {
			directory.mkdirs();
		} else if (directory.isFile()) { // 看看是不是文件夹
			throw new IllegalArgumentException("cannot extract to a file["
					+ directory.getAbsolutePath()
					+ "]. file folder is expected.");
		}
		
		//执行解压缩
		ZipUtil zu = new ZipUtil(inFile.getAbsolutePath(), directory.getAbsolutePath());
		return zu.doRelease();
	}
	
	/**根据返回值确定异常*/
	public static void assertNoException(long flag) throws IOException{		
		switch((int)flag){
			case -1:
				throw new IOException("您所要解压的文件不存在！") ;
			case -2:
				throw new IOException("您所要解压的文件不能被打开！") ;
			case -3:
				throw new IOException("您所要释放的路径不存在！") ;
			case -4:
				throw new IOException("您所创建文件目录失败！") ;
			case -5:
				throw new IOException("写入文件失败！") ;
			case -6:
				throw new IOException("文件已经存在！") ;
			case -50:
				throw new IOException("文件读取异常！") ;		
		}
	}
	
	/**
	 * 演示函数 
	 */
	public static void main(String args[]) throws Exception {
		long flag; // 返回标志
		String inPath, releasePath;

		// 获得用户输入信息
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("请输入zip文件路径：");
		inPath = "d:/control.zip" ;
		System.out.println("请输入保存路径：");
		releasePath = "d:/test/style2";
		userInput.close();
		
		flag = extractZipFileToDirectory(new File(inPath), new File(releasePath)) ;
		assertNoException(flag) ;
	}

}
