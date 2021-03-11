package socialnetwork.domain.validators;

import socialnetwork.domain.Event;

public class EventValidator implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        String errors="";
        if(entity.getId() == null)
            errors+="Id can't be null!\n";
        if(entity.getOwner().getId() == null)
            errors+="Event must have an owner\n";
        if(entity.getDescription().length()<3)
            errors+="Description must have at least three characters\n";
        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
