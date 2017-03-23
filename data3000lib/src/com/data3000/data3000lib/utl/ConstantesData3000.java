package com.data3000.data3000lib.utl;

public class ConstantesData3000 {

	static public final String ATRIBUTO_MAPA_DIR = "mapaDir";
	public static final String ATRIBUTO_LISTA_DIR = "listaDir";
	public static final String ATRIBUTO_ACL = "acl";
	public static final Object SERIE = "SER";
	
	
	/**
	 * Variables del entorno 
	 */
	
	public static final String RUTA_ARCHIVOS = "RUTA_ARCHIVOS"; 
	
	
	/* ==========================================================
	 *                         ERRORES
	 * ==========================================================
	 */
	
	/**
	 * Archivo no existente
	 */
	public static final String ERR1003 = "error.1003";
	
	/**
	 * Error de validación del checksum
	 */
	public static final String ERR1004 = "error.1004";
	
	/**
	 * Solo el propietario puede realizar la acción
	 */
	public static final String ERR1005 = "error.1005";
	
	/**
	 * solo se pueden crear directorios dentro de dependencias
	 *
	 */
	public static final String ERR1007 = "error.1007";
	
	/**
	 * solo se pueden crear dependencias dentro de entidades o las mismas dependencias
	 */
	public static final String ERR1010 = "error.1010";
	
	public static final String ERR1011 = "error.1011";
	
	/* ==========================================================
	 *                    NOMBRE DE BEANS
	 * ==========================================================
	 */
	public static final String BEAN_SISTEMA = "sistemaArchivoNgc";
	
	
	/* ==========================================================
	 *              CONSTANTES DE SISTEMA DE ARCHIVOS
	 * ==========================================================
	 */
	
	public static final String SISTEMA_ARCHIVO_ENTIDAD = "ENT";
	
	public static final String SISTEMA_ARCHIVO_DEPENDENCIA = "DEP";
	
	public static final String SISTEMA_ARCHIVO_DIRECTORIO = "DIR";
	
	
	
}
