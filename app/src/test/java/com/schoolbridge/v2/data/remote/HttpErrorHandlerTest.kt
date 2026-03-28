package com.schoolbridge.v2.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HttpErrorHandlerTest {

    @Test
    fun `fromStatus uses friendly fallback for forbidden`() {
        val error = fromStatus(
            statusCode = 403,
            responseBody = """{"error":"Forbidden"}""",
            defaultMessage = "Something went wrong."
        )

        assertEquals("You do not have permission to do that.", error.message)
        assertEquals(403, error.statusCode)
    }

    @Test
    fun `fromStatus keeps useful server message when it is human readable`() {
        val error = fromStatus(
            statusCode = 422,
            responseBody = """{"message":"Please choose a valid student before submitting."}""",
            defaultMessage = "Something went wrong."
        )

        assertEquals("Please choose a valid student before submitting.", error.message)
        assertEquals(422, error.statusCode)
    }

    @Test
    fun `fromStatus falls back to default message for unknown status without readable body`() {
        val error = fromStatus(
            statusCode = 499,
            responseBody = null,
            defaultMessage = "Could not complete the action."
        )

        assertEquals("Could not complete the action.", error.message)
        assertEquals(499, error.statusCode)
    }

    @Test
    fun `fromStatus maps server failures to a reassuring message`() {
        val error = fromStatus(
            statusCode = 500,
            responseBody = """{"error":"Internal Server Error"}""",
            defaultMessage = "Could not complete the action."
        )

        assertEquals("The server is having a problem right now. Please try again soon.", error.message)
        assertEquals(500, error.statusCode)
    }
}
