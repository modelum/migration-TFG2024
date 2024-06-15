/**
 */
package documentschema;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link documentschema.EntityType#getName <em>Name</em>}</li>
 *   <li>{@link documentschema.EntityType#getProperties <em>Properties</em>}</li>
 *   <li>{@link documentschema.EntityType#getOwner <em>Owner</em>}</li>
 * </ul>
 *
 * @see documentschema.DocumentschemaPackage#getEntityType()
 * @model
 * @generated
 */
public interface EntityType extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see documentschema.DocumentschemaPackage#getEntityType_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link documentschema.EntityType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
	 * The list contents are of type {@link documentschema.Property}.
	 * It is bidirectional and its opposite is '{@link documentschema.Property#getOwner <em>Owner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' containment reference list.
	 * @see documentschema.DocumentschemaPackage#getEntityType_Properties()
	 * @see documentschema.Property#getOwner
	 * @model opposite="owner" containment="true" required="true"
	 * @generated
	 */
	EList<Property> getProperties();

	/**
	 * Returns the value of the '<em><b>Owner</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link documentschema.DocumentSchema#getEntities <em>Entities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owner</em>' container reference.
	 * @see #setOwner(DocumentSchema)
	 * @see documentschema.DocumentschemaPackage#getEntityType_Owner()
	 * @see documentschema.DocumentSchema#getEntities
	 * @model opposite="entities" required="true" transient="false"
	 * @generated
	 */
	DocumentSchema getOwner();

	/**
	 * Sets the value of the '{@link documentschema.EntityType#getOwner <em>Owner</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Owner</em>' container reference.
	 * @see #getOwner()
	 * @generated
	 */
	void setOwner(DocumentSchema value);

} // EntityType
