package com.nitiaayog.apnesaathi.datamanager.role

class RoleImpl : RoleType {

    companion object {
        const val TypeVolunteer: String = "1"
        const val TypeStaffMember: String = "2"
        const val TypeDistrictAdmin: String = "3"
        const val TypeMasterAdmin: String = "4"

        const val RoleVolunteer: String = "role_volunteer"
        const val RoleDistrictAdmin: String = "role_normal_admin"
        const val RoleMasterAdmin: String = "role_master_admin"
        const val RoleStaffMember: String = "role_staff_member"
    }

    override fun getRoleType(type: String): String = when (type) {
        TypeStaffMember -> RoleStaffMember
        TypeMasterAdmin -> RoleMasterAdmin
        TypeDistrictAdmin -> RoleDistrictAdmin
        TypeVolunteer -> RoleVolunteer
        else -> ""
    }
}