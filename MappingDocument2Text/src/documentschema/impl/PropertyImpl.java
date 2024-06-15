/**
 */
package documentschema.impl;

import documentschema.Aggregate;
import documentschema.DocumentschemaPackage;
import documentschema.EntityType;
import documentschema.Property;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link documentschema.impl.PropertyImpl#getName <em>Name</em>}</li>
 *   <li>{@link documentschema.impl.PropertyImpl#getOwner <em>Owner</em>}</li>
 *   <li>{@link documentschema.impl.PropertyImpl#getAggregatedBy <em>Aggregated By</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class PropertyImpl extends MinimalEObjectImpl.Container implements Property {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropertyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DocumentschemaPackage.Literals.PROPERTY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.PROPERTY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityType getOwner() {
		if (eContainerFeatureID() != DocumentschemaPackage.PROPERTY__OWNER) return null;
		return (EntityType)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOwner(EntityType newOwner, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newOwner, DocumentschemaPackage.PROPERTY__OWNER, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOwner(EntityType newOwner) {
		if (newOwner != eInternalContainer() || (eContainerFeatureID() != DocumentschemaPackage.PROPERTY__OWNER && newOwner != null)) {
			if (EcoreUtil.isAncestor(this, newOwner))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newOwner != null)
				msgs = ((InternalEObject)newOwner).eInverseAdd(this, DocumentschemaPackage.ENTITY_TYPE__PROPERTIES, EntityType.class, msgs);
			msgs = basicSetOwner(newOwner, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.PROPERTY__OWNER, newOwner, newOwner));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Aggregate getAggregatedBy() {
		if (eContainerFeatureID() != DocumentschemaPackage.PROPERTY__AGGREGATED_BY) return null;
		return (Aggregate)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAggregatedBy(Aggregate newAggregatedBy, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newAggregatedBy, DocumentschemaPackage.PROPERTY__AGGREGATED_BY, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAggregatedBy(Aggregate newAggregatedBy) {
		if (newAggregatedBy != eInternalContainer() || (eContainerFeatureID() != DocumentschemaPackage.PROPERTY__AGGREGATED_BY && newAggregatedBy != null)) {
			if (EcoreUtil.isAncestor(this, newAggregatedBy))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newAggregatedBy != null)
				msgs = ((InternalEObject)newAggregatedBy).eInverseAdd(this, DocumentschemaPackage.AGGREGATE__AGGREGATES, Aggregate.class, msgs);
			msgs = basicSetAggregatedBy(newAggregatedBy, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.PROPERTY__AGGREGATED_BY, newAggregatedBy, newAggregatedBy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__OWNER:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetOwner((EntityType)otherEnd, msgs);
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetAggregatedBy((Aggregate)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__OWNER:
				return basicSetOwner(null, msgs);
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				return basicSetAggregatedBy(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case DocumentschemaPackage.PROPERTY__OWNER:
				return eInternalContainer().eInverseRemove(this, DocumentschemaPackage.ENTITY_TYPE__PROPERTIES, EntityType.class, msgs);
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				return eInternalContainer().eInverseRemove(this, DocumentschemaPackage.AGGREGATE__AGGREGATES, Aggregate.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__NAME:
				return getName();
			case DocumentschemaPackage.PROPERTY__OWNER:
				return getOwner();
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				return getAggregatedBy();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__NAME:
				setName((String)newValue);
				return;
			case DocumentschemaPackage.PROPERTY__OWNER:
				setOwner((EntityType)newValue);
				return;
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				setAggregatedBy((Aggregate)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DocumentschemaPackage.PROPERTY__OWNER:
				setOwner((EntityType)null);
				return;
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				setAggregatedBy((Aggregate)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DocumentschemaPackage.PROPERTY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DocumentschemaPackage.PROPERTY__OWNER:
				return getOwner() != null;
			case DocumentschemaPackage.PROPERTY__AGGREGATED_BY:
				return getAggregatedBy() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //PropertyImpl
