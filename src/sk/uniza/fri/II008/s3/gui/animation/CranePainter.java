package sk.uniza.fri.II008.s3.gui.animation;

import java.awt.Color;

import sk.uniza.fri.II008.s3.model.Crane;
import sk.uniza.fri.II008.s3.model.requests.CraneRequest;

public class CranePainter {
	private PaintingContext context;
	
	public CranePainter(PaintingContext context)
	{
		this.context = context;
	}
	
	public void paint(Crane crane)
	{
		if(!crane.hasCraneRequest()) {
			return;
		}
		
		CraneRequest request = crane.getCraneRequest();
		
		FactoryPosition from = context.figureOutPositionFor(request.getFrom());
		FactoryPosition to = context.figureOutPositionFor(request.getTo());
		
		int lineStartX = context.view.transformX(from.getX());
		int lineStartY = context.view.transformY(from.getY());
		int lineEndX = context.view.transformX(to.getX());
		int lineEndY = context.view.transformY(to.getY());
		
		DirectTrajectory trajectory = new DirectTrajectory(from, to);
		double amount = (context.replication.currentTime() - request.getStartTimestamp() ) / request.getDuration();
		amount = Util.clip(amount, 0, 1);
		
		FactoryPosition rollPosition = trajectory.getInterpolated((float)amount);
		int rollX = context.view.transformX(rollPosition.getX());
		int rollY = context.view.transformY(rollPosition.getY());
		
		context.graphics.setColor(Color.GRAY.darker());
		context.graphics.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);
		context.graphics.setColor(context.getRollColor(request.getRoll().getType()));
		context.fillCircle(rollX, rollY, 5);
	}
}
