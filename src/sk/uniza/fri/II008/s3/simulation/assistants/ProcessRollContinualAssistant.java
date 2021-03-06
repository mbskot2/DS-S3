package sk.uniza.fri.II008.s3.simulation.assistants;

import OSPABA.Agent;
import OSPABA.MessageForm;
import sk.uniza.fri.II008.generators.IContinuosGenerator;
import sk.uniza.fri.II008.s3.FactorySimulation;
import sk.uniza.fri.II008.s3.model.requests.EmployeeWorkRequest;
import sk.uniza.fri.II008.s3.simulation.ComponentType;
import sk.uniza.fri.II008.s3.simulation.MessageType;
import sk.uniza.fri.II008.s3.simulation.messages.ProcessRollMessage;

public class ProcessRollContinualAssistant extends BaseContinualAssistant
{
	private final IContinuosGenerator processRollDurationGen;

	public ProcessRollContinualAssistant(Agent agent)
	{
		super(ComponentType.PROCESS_ROLL_CONTINUAL_ASSISTANT, agent.mySim(), agent);

		processRollDurationGen = ((IContinuosGenerator) getFactorySimulation().getGenerator(
			FactorySimulation.Generator.PROCESS_ROLL_DURATION));

		agent.addOwnMessage(MessageType.PROCESS_ROLL_DONE);
	}

	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
			case MessageType.start:
				onStartMessageReceived((ProcessRollMessage) message);
				break;
			case MessageType.PROCESS_ROLL_DONE:
				onProcessRollDoneMessageReceived((ProcessRollMessage) message);
				break;
		}
	}

	private void onStartMessageReceived(ProcessRollMessage processRollMessage)
	{
		double duration = processRollDurationGen.nextValue();

		EmployeeWorkRequest employeeWorkRequest = new EmployeeWorkRequest(
			_mySim.currentTime(), duration, processRollMessage.getRoll());

		processRollMessage.getEmployee().setEmployeeRequest(employeeWorkRequest);

		processRollMessage.setCode(MessageType.PROCESS_ROLL_DONE);
		hold(duration, processRollMessage);

		if (getFactorySimulation().isEnabledLogging())
		{
			getFactoryReplication().log(String.format(
				"ProcessRollContinualAssistant[start]\n - employee %s processing roll %s",
				processRollMessage.getEmployee(), processRollMessage.getRoll()));
		}
	}

	private void onProcessRollDoneMessageReceived(ProcessRollMessage processRollMessage)
	{
		if (getFactorySimulation().isEnabledLogging())
		{
			getFactoryReplication().log(String.format(
				"ProcessRollContinualAssistant[PROCESS_ROLL_DONE]\n - roll %s processed",
				processRollMessage.getRoll()));
		}

		processRollMessage.getEmployee().removeEmployeeRequest();

		assistantFinished(processRollMessage);
	}
}
