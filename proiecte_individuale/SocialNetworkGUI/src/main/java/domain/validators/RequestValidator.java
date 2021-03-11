package socialnetwork.domain.validators;

import socialnetwork.domain.Request;

public class RequestValidator implements Validator<Request> {
    /**
     * validate a Request object
     * @param entity Request object
     * @throws ValidationException if we have a invalid entity
     */
    @Override
    public void validate(Request entity) throws ValidationException {
        String errors="";
        if(!entity.getStatus().equals("pending"))
            if(!entity.getStatus().equals("accepted"))
                if(!entity.getStatus().equals("rejected"))
                    errors+="Status must be pending or rejected or accepted\n";
        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
