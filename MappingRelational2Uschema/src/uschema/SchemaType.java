/**
 */
package uschema;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uschema.SchemaType#getName <em>Name</em>}</li>
 *   <li>{@link uschema.SchemaType#getParents <em>Parents</em>}</li>
 *   <li>{@link uschema.SchemaType#getFeatures <em>Features</em>}</li>
 * </ul>
 *
 * @see uschema.UschemaPackage#getSchemaType()
 * @model abstract="true"
 * @generated
 */
public interface SchemaType extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see uschema.UschemaPackage#getSchemaType_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link uschema.SchemaType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Parents</b></em>' reference list.
	 * The list contents are of type {@link uschema.SchemaType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parents</em>' reference list.
	 * @see uschema.UschemaPackage#getSchemaType_Parents()
	 * @model
	 * @generated
	 */
	EList<SchemaType> getParents();

	/**
	 * Returns the value of the '<em><b>Features</b></em>' containment reference list.
	 * The list contents are of type {@link uschema.Feature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Features</em>' containment reference list.
	 * @see uschema.UschemaPackage#getSchemaType_Features()
	 * @model containment="true"
	 * @generated
	 */
	EList<Feature> getFeatures();

} // SchemaType
