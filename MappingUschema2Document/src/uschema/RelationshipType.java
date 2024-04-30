/**
 */
package uschema;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relationship Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uschema.RelationshipType#getReference <em>Reference</em>}</li>
 * </ul>
 *
 * @see uschema.UschemaPackage#getRelationshipType()
 * @model
 * @generated
 */
public interface RelationshipType extends SchemaType {
	/**
	 * Returns the value of the '<em><b>Reference</b></em>' reference list.
	 * The list contents are of type {@link uschema.Reference}.
	 * It is bidirectional and its opposite is '{@link uschema.Reference#getIsFeaturedBy <em>Is Featured By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference</em>' reference list.
	 * @see uschema.UschemaPackage#getRelationshipType_Reference()
	 * @see uschema.Reference#getIsFeaturedBy
	 * @model opposite="isFeaturedBy" required="true"
	 * @generated
	 */
	EList<Reference> getReference();

} // RelationshipType
