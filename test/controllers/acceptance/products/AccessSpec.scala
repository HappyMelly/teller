package controllers.acceptance.products

import controllers.Products
import integration.PlayAppSpec
import models.UserRole.{ DynamicRole, Role }
import stubs.{FakeRuntimeEnvironment, AccessCheckSecurity}

/**
 * Tests methods accessibility
 */
class AccessSpec extends PlayAppSpec {
  class TestProducts() extends Products(FakeRuntimeEnvironment)
    with AccessCheckSecurity


  val controller = new TestProducts

  "Method 'activation'" should {
    "have Admin access rights" in {
      controller.activation(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'add'" should {
    "have Admin access rights" in {
      controller.add().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'addBrand'" should {
    "have Admin access rights" in {
      controller.addBrand().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'index'" should {
    "have Admin access rights" in {
      controller.create().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'delete'" should {
    "have Admin access rights" in {
      controller.delete(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'deleteBrand'" should {
    "have Admin access rights" in {
      controller.deleteBrand("test", 1L, 1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'deletePicture'" should {
    "have Admin access rights" in {
      controller.deletePicture(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'details'" should {
    "have Viewer access rights" in {
      controller.details(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
  "Method 'edit'" should {
    "have Admin access rights" in {
      controller.edit(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'index'" should {
    "have Viewer access rights" in {
      controller.index().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
  "Method 'update'" should {
    "have Admin access rights" in {
      controller.update(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

}
