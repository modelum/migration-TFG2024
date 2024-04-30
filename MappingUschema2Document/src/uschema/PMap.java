/**
 */
package uschema;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>PMap</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uschema.PMap#getKeyType <em>Key Type</em>}</li>
 *   <li>{@link uschema.PMap#getValueType <em>Value Type</em>}</li>
 * </ul>
 *
 * @see uschema.UschemaPackage#getPMap()
 * @model
 * @generated
 */
public interface PMap extends DataType {
	/**
	 * Returns the value of the '<em><b>Key Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key Type</em>' containment reference.
	 * @see #setKeyType(PrimitiveType)
	 * @see uschema.UschemaPackage#getPMap_KeyType()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PrimitiveType getKeyType();

	/**
	 * Sets the value of the '{@link uschema.PMap#getKeyType <em>Key Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key Type</em>' containment reference.
	 * @see #getKeyType()
	 * @generated
	 */
	void setKeyType(PrimitiveType value);

	/**
	 * Returns the value of the '<em><b>Value Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value Type</em>' containment reference.
	 * @see #setValueType(DataType)
	 * @see uschema.UschemaPackage#getPMap_ValueType()
	 * @model containment="true" required="true"
	 * @generated
	 */
	DataType getValueType();

	/**
	 * Sets the value of the '{@link uschema.PMap#getValueType <em>Value Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Type</em>' containment reference.
	 * @see #getValueType()
	 * @generated
	 */
	void setValueType(DataType value);

} // PMap
