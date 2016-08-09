public class NBody {

	public static double readRadius(String file) {
		In in =  new In(file);

		in.readInt();	//discarded
		return in.readDouble();
	}

	public static Planet[] readPlanets(String file) {
		In in = new In(file);
		Planet[] planets = new Planet[5];

		in.readInt();		//discarded
		in.readDouble();	//discarded

		int counter = 0;
		while(!in.isEmpty()) {
			double xP = in.readDouble();
			double yP = in.readDouble();
			double xV = in.readDouble();
			double yV = in.readDouble();
			double mass = in.readDouble();
			String f = in.readString();

			planets[counter] = new Planet(xP, yP, xV, yV, mass, f);
			counter += 1;
		}
		return planets;
	}

	public static void main(String[] args) {
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];
		double uRadius = readRadius(filename);
		Planet[] planets = readPlanets(filename);
		String background = "images/starfield.jpg";

		StdDraw.setScale(-uRadius, uRadius);
		StdDraw.clear();
		//draw background
		StdDraw.picture(0, 0, background);

		//draw each planet in the array
		for(Planet p : planets) {
			p.draw();
		}

		double t = 0;	//time

		while(t != T) {
			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];

            //populate the xForces and yForces arrays with the forces
			for(int i = 0; i < planets.length; ++i) {
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
                yForces[i] = planets[i].calcNetForceExertedByY(planets);
			}

			//update the values of all planets
			for(int i = 0; i < planets.length; ++i) {
			    planets[i].update(dt, xForces[i], yForces[i]);
            }

            StdDraw.picture(0, 0, background);

            for(Planet p : planets) {
                p.draw();
            }

            StdDraw.show(10);

            t += dt;
		}

	}
}