package com.schoolbridge.v2.data.api

import com.schoolbridge.v2.data.dto.school.CampusDto
import com.schoolbridge.v2.data.dto.school.CollegeDto
import com.schoolbridge.v2.data.dto.school.DepartmentDto
import com.schoolbridge.v2.data.dto.school.BuildingDto
import com.schoolbridge.v2.data.dto.school.ClassroomDto
import com.schoolbridge.v2.data.dto.geo.DistrictDto
import com.schoolbridge.v2.data.dto.geo.ProvinceDto
import com.schoolbridge.v2.data.dto.geo.SectorDto
import com.schoolbridge.v2.data.dto.school.SchoolDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.Query // For potential filtering/searching

/**
 * Retrofit API interface for **School-related operations**.
 * This interface defines the endpoints for retrieving and managing data related to
 * schools, their campuses, academic structures (colleges, departments), and geographical
 * administrative divisions (provinces, districts, sectors).
 *
 * It acts as the contract for how your application interacts with the backend for
 * all school-specific information.
 */
interface SchoolApi {

    // --- School Endpoints ---

    /**
     * Fetches a list of all registered schools.
     * This endpoint is typically used to populate a directory or a search feature.
     *
     * @param schoolType An optional query parameter to filter schools by type (e.g., "UNIVERSITY", "HIGH_SCHOOL").
     * @param provinceId An optional query parameter to filter schools by the province they are in.
     * @return A [List] of [SchoolDto] objects.
     *
     * **Example Use:**
     * `schoolApi.getAllSchools()` // Get all schools
     * `schoolApi.getAllSchools(schoolType = "UNIVERSITY")` // Get all universities
     * `schoolApi.getAllSchools(provinceId = "PROV001")` // Get all schools in a specific province
     */
    @GET("schools")
    suspend fun getAllSchools(
        @Query("schoolType") schoolType: String? = null,
        @Query("provinceId") provinceId: String? = null
    ): List<SchoolDto>

    /**
     * Fetches details of a specific school by its ID.
     *
     * @param schoolId The unique identifier of the school to retrieve.
     * @return A [SchoolDto] representing the requested school.
     *
     * **Example Use:**
     * `val universityOfRwanda = schoolApi.getSchoolById("SCHL_UNIRW")`
     */
    @GET("schools/{schoolId}")
    suspend fun getSchoolById(@Path("schoolId") schoolId: String): SchoolDto

    /**
     * Creates a new school record.
     * This endpoint would typically be protected and only accessible by super-administrators.
     *
     * @param schoolDto The [SchoolDto] containing the details of the new school to create.
     * The `id` field in the DTO might be ignored by the backend or used as a hint.
     * @return The created [SchoolDto] with its generated ID and other server-side populated fields.
     *
     * **Example Use:**
     * ```kotlin
     * val newSchool = SchoolDto(
     * id = "", // Backend will generate this
     * name = "Example Primary School",
     * schoolType = "PRIMARY_SCHOOL",
     * code = "EPS001",
     * address = AddressDto(street = "123 School Rd", city = "Sampletown", country = "Rwanda"),
     * phoneNumber = null, email = null, websiteUrl = null, logoUrl = null,
     * establishedDate = "2023-09-01", enrollmentCount = 0, status = "ACTIVE"
     * )
     * val createdSchool = schoolApi.createSchool(newSchool)
     * ```
     */
    @POST("schools")
    suspend fun createSchool(@Body schoolDto: SchoolDto): SchoolDto

    /**
     * Updates an existing school record.
     * This endpoint would typically be protected and only accessible by authorized administrators.
     *
     * @param schoolId The unique identifier of the school to update.
     * @param schoolDto The [SchoolDto] containing the updated details. Only fields provided in the DTO
     * (non-null) will be updated if using a partial update strategy.
     * @return The updated [SchoolDto].
     *
     * **Example Use:**
     * ```kotlin
     * val schoolToUpdate = schoolApi.getSchoolById("SCHL_UNIRW")
     * val updatedSchoolInfo = schoolToUpdate.copy(email = "info@ur.ac.rw", phoneNumber = "+250788123000")
     * val result = schoolApi.updateSchool("SCHL_UNIRW", updatedSchoolInfo)
     * ```
     */
    @PUT("schools/{schoolId}")
    suspend fun updateSchool(@Path("schoolId") schoolId: String, @Body schoolDto: SchoolDto): SchoolDto


    // --- Campus Endpoints ---

    /**
     * Fetches all campuses belonging to a specific school.
     *
     * @param schoolId The ID of the school whose campuses are to be retrieved.
     * @return A [List] of [CampusDto] objects.
     *
     * **Example Use:**
     * `val urCampuses = schoolApi.getCampusesBySchoolId("SCHL_UNIRW")`
     */
    @GET("schools/{schoolId}/campuses")
    suspend fun getCampusesBySchoolId(@Path("schoolId") schoolId: String): List<CampusDto>

    /**
     * Fetches details of a specific campus by its ID.
     *
     * @param schoolId The ID of the parent school (for hierarchical routing, or just for validation).
     * @param campusId The unique identifier of the campus to retrieve.
     * @return A [CampusDto] representing the requested campus.
     *
     * **Example Use:**
     * `val gikondoCampus = schoolApi.getCampusById("SCHL_UNIRW", "CAMP_GIKONDO")`
     */
    @GET("schools/{schoolId}/campuses/{campusId}")
    suspend fun getCampusById(
        @Path("schoolId") schoolId: String,
        @Path("campusId") campusId: String
    ): CampusDto

    /**
     * Creates a new campus for a specific school.
     *
     * @param schoolId The ID of the school to which the new campus will belong.
     * @param campusDto The [CampusDto] containing the details of the new campus.
     * @return The created [CampusDto] with its generated ID.
     *
     * **Example Use:**
     * ```kotlin
     * val newCampus = CampusDto(
     * id = "", // Backend will generate this
     * name = "Huye Campus",
     * schoolId = "SCHL_UNIRW",
     * address = AddressDto(street = "Butare Rd", city = "Huye", country = "Rwanda"),
     * description = null, contactPhone = null, contactEmail = null
     * )
     * val createdCampus = schoolApi.createCampus("SCHL_UNIRW", newCampus)
     * ```
     */
    @POST("schools/{schoolId}/campuses")
    suspend fun createCampus(@Path("schoolId") schoolId: String, @Body campusDto: CampusDto): CampusDto

    // --- College Endpoints (Specific to Universities) ---

    /**
     * Fetches all colleges within a specific university.
     * This is typically applicable to schools with a 'UNIVERSITY' school type.
     *
     * @param schoolId The ID of the university whose colleges are to be retrieved.
     * @return A [List] of [CollegeDto] objects.
     *
     * **Example Use:**
     * `val urColleges = schoolApi.getCollegesBySchoolId("SCHL_UNIRW")`
     */
    @GET("schools/{schoolId}/colleges")
    suspend fun getCollegesBySchoolId(@Path("schoolId") schoolId: String): List<CollegeDto>

    /**
     * Fetches details of a specific college by its ID.
     *
     * @param schoolId The ID of the parent university.
     * @param collegeId The unique identifier of the college to retrieve.
     * @return A [CollegeDto] representing the requested college.
     *
     * **Example Use:**
     * `val cbeCollege = schoolApi.getCollegeById("SCHL_UNIRW", "CBE")`
     */
    @GET("schools/{schoolId}/colleges/{collegeId}")
    suspend fun getCollegeById(
        @Path("schoolId") schoolId: String,
        @Path("collegeId") collegeId: String
    ): CollegeDto

    /**
     * Fetches all colleges located on a specific campus.
     * This helps in displaying colleges associated with a physical location.
     *
     * @param schoolId The ID of the parent university.
     * @param campusId The ID of the campus.
     * @return A [List] of [CollegeDto] objects.
     *
     * **Example Use:**
     * `val gikondoColleges = schoolApi.getCollegesByCampusId("SCHL_UNIRW", "CAMP_GIKONDO")`
     */
    @GET("schools/{schoolId}/campuses/{campusId}/colleges")
    suspend fun getCollegesByCampusId(
        @Path("schoolId") schoolId: String,
        @Path("campusId") campusId: String
    ): List<CollegeDto>

    // --- Department Endpoints ---

    /**
     * Fetches all departments within a specific school (or university/college).
     * This endpoint might be used for general department listings.
     *
     * @param schoolId The ID of the school.
     * @return A [List] of [DepartmentDto] objects.
     *
     * **Example Use:**
     * `val urDepartments = schoolApi.getDepartmentsBySchoolId("SCHL_UNIRW")`
     */
    @GET("schools/{schoolId}/departments")
    suspend fun getDepartmentsBySchoolId(@Path("schoolId") schoolId: String): List<DepartmentDto>

    /**
     * Fetches all departments belonging to a specific college.
     * This is useful for hierarchical navigation, e.g., showing departments within CBE.
     *
     * @param schoolId The ID of the parent university.
     * @param collegeId The ID of the college whose departments are to be retrieved.
     * @return A [List] of [DepartmentDto] objects.
     *
     * **Example Use:**
     * `val cbeDepartments = schoolApi.getDepartmentsByCollegeId("SCHL_UNIRW", "CBE")`
     */
    @GET("schools/{schoolId}/colleges/{collegeId}/departments")
    suspend fun getDepartmentsByCollegeId(
        @Path("schoolId") schoolId: String,
        @Path("collegeId") collegeId: String
    ): List<DepartmentDto>

    // --- Building Endpoints ---

    /**
     * Fetches all buildings within a specific school.
     *
     * @param schoolId The ID of the school.
     * @return A [List] of [BuildingDto] objects.
     */
    @GET("schools/{schoolId}/buildings")
    suspend fun getBuildingsBySchoolId(@Path("schoolId") schoolId: String): List<BuildingDto>

    /**
     * Fetches all buildings within a specific campus.
     *
     * @param schoolId The ID of the parent school.
     * @param campusId The ID of the campus.
     * @return A [List] of [BuildingDto] objects.
     */
    @GET("schools/{schoolId}/campuses/{campusId}/buildings")
    suspend fun getBuildingsByCampusId(
        @Path("schoolId") schoolId: String,
        @Path("campusId") campusId: String
    ): List<BuildingDto>

    // --- Classroom Endpoints ---

    /**
     * Fetches all classrooms within a specific school.
     *
     * @param schoolId The ID of the school.
     * @return A [List] of [ClassroomDto] objects.
     */
    @GET("schools/{schoolId}/classrooms")
    suspend fun getClassroomsBySchoolId(@Path("schoolId") schoolId: String): List<ClassroomDto>

    /**
     * Fetches all classrooms within a specific building.
     *
     * @param schoolId The ID of the parent school.
     * @param buildingId The ID of the building.
     * @return A [List] of [ClassroomDto] objects.
     */
    @GET("schools/{schoolId}/buildings/{buildingId}/classrooms")
    suspend fun getClassroomsByBuildingId(
        @Path("schoolId") schoolId: String,
        @Path("buildingId") buildingId: String
    ): List<ClassroomDto>


    // --- Geographical/Administrative Endpoints (assuming these are school-related Lookups) ---

    /**
     * Fetches all provinces.
     * This is generally a static lookup table for geographical data.
     *
     * @return A [List] of [ProvinceDto] objects.
     *
     * **Example Use:**
     * `val provinces = schoolApi.getAllProvinces()` // Used to populate a dropdown for school location.
     */
    @GET("provinces")
    suspend fun getAllProvinces(): List<ProvinceDto>

    /**
     * Fetches all districts within a specific province.
     *
     * @param provinceId The ID of the province.
     * @return A [List] of [DistrictDto] objects.
     *
     * **Example Use:**
     * `val districtsInKigali = schoolApi.getDistrictsByProvinceId("PROV001")`
     */
    @GET("provinces/{provinceId}/districts")
    suspend fun getDistrictsByProvinceId(@Path("provinceId") provinceId: String): List<DistrictDto>

    /**
     * Fetches all sectors within a specific district.
     *
     * @param districtId The ID of the district.
     * @return A [List] of [SectorDto] objects.
     *
     * **Example Use:**
     * `val sectorsInKicukiro = schoolApi.getSectorsByDistrictId("DST005")`
     */
    @GET("districts/{districtId}/sectors")
    suspend fun getSectorsByDistrictId(@Path("districtId") districtId: String): List<SectorDto>

    // TODO: Add more endpoints as your needs evolve, e.g.:
    // - POST/PUT/DELETE for Campus, College, Department, Building, Classroom
    // - Endpoints for specific academic levels (e.g., primary, high school specific programs)
    // - Search functionalities with more complex filters for schools, campuses, etc.
}