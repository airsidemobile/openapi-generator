/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
*/
package org.openapitools.api

import org.openapitools.model.Order
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Authorization
import io.swagger.annotations.AuthorizationScope
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import kotlin.collections.List
import kotlin.collections.Map

@Validated
@Api(value = "store", description = "The store API")
@RequestMapping("\${api.base-path:/v2}")
interface StoreApi {

    fun getDelegate(): StoreApiDelegate = object: StoreApiDelegate {}

    @ApiOperation(
        value = "Delete purchase order by ID",
        nickname = "deleteOrder",
        notes = "For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors")
    @ApiResponses(
        value = [ApiResponse(code = 400, message = "Invalid ID supplied"),ApiResponse(code = 404, message = "Order not found")])
    @DeleteMapping(
            value = ["/store/order/{orderId}"]
    )
    fun deleteOrder(@ApiParam(value = "ID of the order that needs to be deleted", required=true) @PathVariable("orderId") orderId: kotlin.String
): ResponseEntity<Unit> {
        return getDelegate().deleteOrder(orderId);
    }

    @ApiOperation(
        value = "Returns pet inventories by status",
        nickname = "getInventory",
        notes = "Returns a map of status codes to quantities",
        response = kotlin.Int::class,
        responseContainer = "Map",
        authorizations = [Authorization(value = "api_key")])
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "successful operation", response = kotlin.collections.Map::class, responseContainer = "Map")])
    @GetMapping(
            value = ["/store/inventory"],
            produces = ["application/json"]
    )
    fun getInventory(): ResponseEntity<Map<String, kotlin.Int>> {
        return getDelegate().getInventory();
    }

    @ApiOperation(
        value = "Find purchase order by ID",
        nickname = "getOrderById",
        notes = "For valid response try integer IDs with value <= 5 or > 10. Other values will generated exceptions",
        response = Order::class)
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "successful operation", response = Order::class),ApiResponse(code = 400, message = "Invalid ID supplied"),ApiResponse(code = 404, message = "Order not found")])
    @GetMapping(
            value = ["/store/order/{orderId}"],
            produces = ["application/xml", "application/json"]
    )
    fun getOrderById(@Min(1L) @Max(5L) @ApiParam(value = "ID of pet that needs to be fetched", required=true) @PathVariable("orderId") orderId: kotlin.Long
): ResponseEntity<Order> {
        return getDelegate().getOrderById(orderId);
    }

    @ApiOperation(
        value = "Place an order for a pet",
        nickname = "placeOrder",
        notes = "",
        response = Order::class)
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "successful operation", response = Order::class),ApiResponse(code = 400, message = "Invalid Order")])
    @PostMapping(
            value = ["/store/order"],
            produces = ["application/xml", "application/json"],
            consumes = ["application/json"]
    )
    fun placeOrder(@ApiParam(value = "order placed for purchasing the pet" ,required=true ) @Valid @RequestBody order: Order
): ResponseEntity<Order> {
        return getDelegate().placeOrder(order);
    }
}
