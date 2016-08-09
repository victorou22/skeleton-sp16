public class Planet{

	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	public Planet(double xP, double yP, double xV, double yV, double m, String img)	{
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}
 
	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	public double calcDistance(Planet p) {
		double dx = xxPos - p.xxPos;
		double dy = yyPos - p.yyPos;

		return Math.sqrt(dx*dx + dy*dy);
	}

	public double calcForceExertedBy(Planet p) {
		return 6.67e-11 * mass * p.mass / (calcDistance(p) * calcDistance(p));
	}

	public double calcForceExertedByX(Planet p) {
		return calcForceExertedBy(p) * (p.xxPos - xxPos) / calcDistance(p);
	}

	public double calcForceExertedByY(Planet p) {
		return calcForceExertedBy(p) * (p.yyPos - yyPos) / calcDistance(p);
	}

	public double calcNetForceExertedByX(Planet[] ps) {
		double total = 0;
		for(Planet p : ps) {
			if(equals(p))	//if at the current planet, continue
				continue;
			total += calcForceExertedByX(p);
		}
		return total;
	}

	public double calcNetForceExertedByY(Planet[] ps) {
		double total = 0;
		for(Planet p : ps) {
			if(equals(p))	//if at the current planet, continue
				continue;
			total += calcForceExertedByY(p);
		}
		return total;
	}

	public void update(double dt, double fX, double fY) {
		double accX = fX / mass;
		double accY = fY / mass;

		//update x and y velocities
		xxVel = xxVel + dt * accX;
		yyVel = yyVel + dt * accY;

		//update x and y positions
		xxPos = xxPos + dt * xxVel;
		yyPos = yyPos + dt * yyVel;
	}

	public void draw() {
		StdDraw.picture(xxPos, yyPos, imgFileName);
	}

}