/**
 */
package uschema;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>USchema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uschema.USchema#getName <em>Name</em>}</li>
 *   <li>{@link uschema.USchema#getEntities <em>Entities</em>}</li>
 *   <li>{@link uschema.USchema#getRelationships <em>Relationships</em>}</li>
 * </ul>
 *
 * @see uschema.UschemaPackage#getUSchema()
 * @model
 * @generated
 */
public interface USchema extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see uschema.UschemaPackage#getUSchema_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link uschema.USchema#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Entities</b></em>' containment reference list.
	 * The list contents are of type {@link uschema.EntityType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entities</em>' containment reference list.
	 * @see uschema.UschemaPackage#getUSchema_Entities()
	 * @model containment="true"
	 * @generated
	 */
	EList<EntityType> getEntities();

	/**
	 * Returns the value of the '<em><b>Relationships</b></em>' containment reference list.
	 * The list contents are of type {@link uschema.RelationshipType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relationships</em>' containment reference list.
	 * @see uschema.UschemaPackage#getUSchema_Relationships()
	 * @model containment="true"
	 * @generated
	 */
	EList<RelationshipType> getRelationships();

} // USchema
