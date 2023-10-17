import NGText from '@components/ng-text/NGText';
import {UNKOWNERROR} from '@/constant/NGContant';

function CatchErrorLoading({
  returnRedux,
  message,
}: {
  returnRedux: any;
  message?: string;
}) {
  if (returnRedux.isSuccess) {
    return <NGText text={'Loading ... '} />;
  } else if (returnRedux.isError) {
    return (
      <NGText
        text={
          'Something went wrong with get ' + message ? message : UNKOWNERROR
        }
      />
    );
  }
}

export default CatchErrorLoading;
