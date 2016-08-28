package com.avaje.tests.iud;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.server.el.ElPropertyValue;
import com.avaje.tests.model.carwheel.Car;
import com.avaje.tests.model.carwheel.Tire;
import com.avaje.tests.model.carwheel.Wheel;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class TestCarWheelIud extends BaseTestCase {

  @Test
	public void test() {

		Car car = new Car();

		Tire t1 = new Tire();
		Wheel w1 = new Wheel();
		w1.setCar(car);
		w1.setTire(t1);
		w1.setPlace("front-left");

		Tire t2 = new Tire();
		Wheel w2 = new Wheel();
		w2.setCar(car);
		w2.setTire(t2);
		w2.setPlace("front-right");
		
		Tire t3 = new Tire();
		Wheel w3 = new Wheel();
		w3.setCar(car);
		w3.setTire(t3);
		w3.setPlace("rear-left");

		Tire t4 = new Tire();
		Wheel w4 = new Wheel();
		w4.setCar(car);
		w4.setTire(t4);
		w4.setPlace("spare");
		
		List<Wheel> wheels = new ArrayList<Wheel>();
		wheels.add(w1);
		wheels.add(w2);
		wheels.add(w3);
		wheels.add(w4);
		
		car.setWheels(wheels);

		Ebean.save(car);
		
		// Do some EL-checking: TODO: File should be reindented.
    ElPropertyValue elValue = getBeanDescriptor(Car.class).getElGetValue("wheels[0].place");
    assertNotNull(elValue);
    assertEquals("front-left",elValue.pathGet(car));

    elValue = getBeanDescriptor(Car.class).getElGetValue("wheels[1].place");
    assertEquals("front-right",elValue.pathGet(car));

    elValue = getBeanDescriptor(Car.class).getElGetValue("wheels[2].place");
    assertEquals("rear-left",elValue.pathGet(car));

    elValue = getBeanDescriptor(Car.class).getElGetValue("wheels[3].place");
    assertEquals("spare",elValue.pathGet(car));
    elValue.pathSet(car, "rear-right");

    assertEquals("rear-right",car.getWheels().get(4).getPlace());
		// And I'm trying to delete this entry with code:
		Car car2 = Ebean.find(Car.class, car.getId());

		Ebean.delete(car2);

	}
}
